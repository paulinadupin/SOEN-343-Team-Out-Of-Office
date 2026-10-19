import RestaurantHeader from "../../components/RestaurantHeader";
import MenuItem from "../../components/MenuItem";
import './Browse.css';

function Browse() {
    return (
        <div className="browse">
            <RestaurantHeader name="Bubble Tea" address="123 Main St, City, Country" status="Open" />
            <h1>Menu</h1>
            <div className="menu-items">
                <MenuItem name="Classic Milk Tea" price="4.99" description="Classic milk tea with tapioca pearls" image="./bubble-tea-test.jpg" />
                <MenuItem name="Milk Tea" price="5.99" description="Milk tea with tapioca pearls" image="./bubble-tea-test.jpg" />
                <MenuItem name="Mango Milk Tea" price="7.99" description="Mango Milk tea with tapioca pearls" image="./bubble-tea-test.jpg" />
            </div>
           
        </div>
    )
}

export default Browse;