package com.enchantea.application.ord;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InOrder;

import com.enchantea.application.ord.contracts.CommandRejectedException;
import com.enchantea.application.ord.contracts.CommandType;
import com.enchantea.application.ord.contracts.CurrentUser;
import com.enchantea.application.ord.contracts.RestaurantQuery;
import com.enchantea.domain.ord.Order;
import com.enchantea.domain.reg.AccountType;

// Characterization test for RestaurantRulesCheck: it pins down what the
// original class does (which rejection, which message, which order, which
// collaborators get called) so every Extract Method step can be checked
// against it. Plain unit test with mocked collaborators on purpose: the
// call-order and "never queried" assertions can't be made against the real
// seeded data that OrderApplicationServiceTests uses.
//
// Lives in the same package as CommandCheck so it can call the protected
// verify(...) directly.
class RestaurantRulesCheckTests {
    private static final Long RESTAURANT_ID = 7L;

    private OrderRepository orders;
    private RestaurantQuery restaurantQuery;
    private RestaurantRulesCheck check;

    private CurrentUser customer;
    private CurrentUser restaurantUser;

    @BeforeEach
    void setUp() {
        orders = mock(OrderRepository.class);
        restaurantQuery = mock(RestaurantQuery.class);
        check = new RestaurantRulesCheck(orders, restaurantQuery);

        customer = new CurrentUser(1L, AccountType.CUSTOMER, null);
        restaurantUser = new CurrentUser(2L, AccountType.RESTAURANT, RESTAURANT_ID);
    }

    private OrderCommands.PlaceOrder place(Long restaurantId, String... itemIds) {
        List<OrderCommands.RequestedLine> lines = java.util.Arrays.stream(itemIds)
                .map(itemId -> new OrderCommands.RequestedLine(itemId, 1))
                .toList();
        return new OrderCommands.PlaceOrder(restaurantId, lines);
    }

    private void verifyPlace(OrderCommands.PlaceOrder command) {
        check.verify(CommandType.PLACE_ORDER, customer, command);
    }

    private void verifyAccept(String orderId) {
        check.verify(CommandType.ACCEPT_ORDER, restaurantUser, new OrderCommands.OrderAction(orderId, null));
    }

    private void givenOpenRestaurantWithItems(String... itemIds) {
        when(restaurantQuery.isOpen(RESTAURANT_ID)).thenReturn(true);
        for (String itemId : itemIds) {
            when(restaurantQuery.isItemAvailable(RESTAURANT_ID, itemId)).thenReturn(true);
        }
    }

    private void givenOrderBelongsTo(String orderId, Long restaurantId) {
        Order order = mock(Order.class);
        when(order.restaurantId()).thenReturn(restaurantId);
        when(orders.findById(orderId)).thenReturn(Optional.of(order));
    }

    private void assertRejected(String expectedMessage, Runnable action) {
        CommandRejectedException rejected = assertThrows(CommandRejectedException.class, action::run);
        assertEquals(expectedMessage, rejected.getMessage());
    }

    //PLACE_ORDER --------------------------------------------------------------------

    @Test
    void aValidOrderIsAccepted() {
        givenOpenRestaurantWithItems("classic-milk-tea", "taro-latte");

        assertDoesNotThrow(() -> verifyPlace(place(RESTAURANT_ID, "classic-milk-tea", "taro-latte")));
    }

    @Test
    void aMissingRestaurantIsRejectedBeforeAnyQueryIsMade() {
        assertRejected("A restaurant is required", () -> verifyPlace(place(null, "classic-milk-tea")));

        verifyNoInteractions(restaurantQuery);
    }

    @ParameterizedTest
    @ValueSource(longs = { 0L, -1L, Long.MIN_VALUE })
    void aNonPositiveRestaurantIdIsRejectedBeforeAnyQueryIsMade(long restaurantId) {
        assertRejected("A restaurant is required", () -> verifyPlace(place(restaurantId, "classic-milk-tea")));

        verifyNoInteractions(restaurantQuery);
    }

    @Test
    void aClosedRestaurantIsRejectedBeforeTheLinesAreChecked() {
        when(restaurantQuery.isOpen(RESTAURANT_ID)).thenReturn(false);

        // the order has no lines either, but "closed" must win because it is checked first
        assertRejected("The restaurant is closed", () -> verifyPlace(place(RESTAURANT_ID)));

        verify(restaurantQuery).isOpen(RESTAURANT_ID);
        verifyNoMoreInteractions(restaurantQuery);
    }

    @Test
    void anOrderWithoutLinesIsRejected() {
        when(restaurantQuery.isOpen(RESTAURANT_ID)).thenReturn(true);

        assertRejected("An order needs at least one item", () -> verifyPlace(place(RESTAURANT_ID)));

        verify(restaurantQuery, never()).isItemAvailable(anyLong(), any());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "   ", "\t" })
    void aBlankItemIdIsRejectedWithoutAskingTheMenu(String itemId) {
        when(restaurantQuery.isOpen(RESTAURANT_ID)).thenReturn(true);

        assertRejected("An item is required", () -> verifyPlace(place(RESTAURANT_ID, itemId)));

        verify(restaurantQuery, never()).isItemAvailable(anyLong(), any());
    }

    @Test
    void anUnavailableItemIsRejectedAndNamedInTheMessage() {
        when(restaurantQuery.isOpen(RESTAURANT_ID)).thenReturn(true);
        when(restaurantQuery.isItemAvailable(RESTAURANT_ID, "sold-out-item")).thenReturn(false);

        assertRejected("Item is not available: sold-out-item",
                () -> verifyPlace(place(RESTAURANT_ID, "sold-out-item")));
    }

    @Test
    void theFirstBadLineStopsTheCheckSoLaterLinesAreNeverQueried() {
        givenOpenRestaurantWithItems("classic-milk-tea");
        when(restaurantQuery.isItemAvailable(RESTAURANT_ID, "sold-out-item")).thenReturn(false);

        assertRejected("Item is not available: sold-out-item",
                () -> verifyPlace(place(RESTAURANT_ID, "classic-milk-tea", "sold-out-item", "taro-latte")));

        verify(restaurantQuery, never()).isItemAvailable(RESTAURANT_ID, "taro-latte");
    }

    @Test
    void aBlankItemAfterAValidOneIsReportedOnlyAfterTheEarlierItemWasChecked() {
        givenOpenRestaurantWithItems("classic-milk-tea");

        assertRejected("An item is required", () -> verifyPlace(place(RESTAURANT_ID, "classic-milk-tea", " ")));

        verify(restaurantQuery).isItemAvailable(RESTAURANT_ID, "classic-milk-tea");
    }

    @Test
    void theRestaurantIsQueriedBeforeTheItemsAndItemsInLineOrder() {
        givenOpenRestaurantWithItems("classic-milk-tea", "taro-latte");

        verifyPlace(place(RESTAURANT_ID, "classic-milk-tea", "taro-latte"));

        InOrder queries = inOrder(restaurantQuery);
        queries.verify(restaurantQuery).isOpen(RESTAURANT_ID);
        queries.verify(restaurantQuery).isItemAvailable(RESTAURANT_ID, "classic-milk-tea");
        queries.verify(restaurantQuery).isItemAvailable(RESTAURANT_ID, "taro-latte");
        queries.verifyNoMoreInteractions();
    }

    @Test
    void placingAnOrderNeverTouchesTheOrderRepository() {
        givenOpenRestaurantWithItems("classic-milk-tea");

        verifyPlace(place(RESTAURANT_ID, "classic-milk-tea"));

        verifyNoInteractions(orders);
    }

    //ACCEPT_ORDER ----------------------------------------------------------------

    @Test
    void anOrderIsAcceptedWhileItsRestaurantIsOpen() {
        givenOrderBelongsTo("order-1", RESTAURANT_ID);
        when(restaurantQuery.isOpen(RESTAURANT_ID)).thenReturn(true);

        assertDoesNotThrow(() -> verifyAccept("order-1"));
    }

    @Test
    void acceptingIsRejectedWhenTheRestaurantIsClosed() {
        givenOrderBelongsTo("order-1", RESTAURANT_ID);
        when(restaurantQuery.isOpen(RESTAURANT_ID)).thenReturn(false);

        assertRejected("The restaurant is closed", () -> verifyAccept("order-1"));
    }

    @Test
    void acceptingChecksTheRestaurantOfTheOrderNotAnyOther() {
        givenOrderBelongsTo("order-1", 42L);
        when(restaurantQuery.isOpen(42L)).thenReturn(true);

        verifyAccept("order-1");

        verify(restaurantQuery).isOpen(42L);
        verify(restaurantQuery, never()).isOpen(RESTAURANT_ID);
    }

    @Test
    void acceptingAnUnknownOrderFailsAsNotFoundWithoutCheckingAnyRestaurant() {
        when(orders.findById("missing")).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> verifyAccept("missing"));

        verifyNoInteractions(restaurantQuery);
    }

    @ParameterizedTest
    @EnumSource(value = CommandType.class, mode = EnumSource.Mode.EXCLUDE, names = { "PLACE_ORDER", "ACCEPT_ORDER" })
    void otherCommandTypesAreNotChecked(CommandType type) {
        assertDoesNotThrow(() -> check.verify(type, operatorFor(type), new Object()));

        verifyNoInteractions(orders, restaurantQuery);
    }

    private CurrentUser operatorFor(CommandType type) {
        return new CurrentUser(3L, AccountType.OPERATOR, null);
    }
}