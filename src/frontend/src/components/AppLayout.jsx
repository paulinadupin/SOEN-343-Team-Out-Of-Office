import Header from "./Header";
import RestaurantHeader from "./RestaurantHeader";
import MenuItem from "./MenuItem";
import { Outlet } from "react-router-dom";

function AppLayout(){
    return(
        <div>
            <Header />
            <Outlet />
        </div>
    )
}

export default AppLayout;