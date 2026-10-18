export const ROLES = { CUSTOMER: "customer", RESTAURANT: "restaurant", OPERATOR: "operator", COURIER: "courier" };

export const HOME_BY_ROLE = {
  [ROLES.CUSTOMER]: "/",
  [ROLES.RESTAURANT]: "/dashboard",
  [ROLES.COURIER]: "/courier",
  [ROLES.OPERATOR]: "/operator",
};