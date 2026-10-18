import './App.css'
import Header from './Header.jsx'
import MenuItem from './MenuItem.jsx'
import RestaurantHeader from './RestaurantHeader.jsx'

function App() {
  return (
    <div>
      <Header />
        <RestaurantHeader name="Mimi's Tea" address="123 Main St, City, Country" status="open" />
        <div className="app-content">
          <MenuItem name="Milk Tea" price="5.99" description="Milk tea with tapioca pearls" image="./bubble-tea-test.jpg" />
          <MenuItem name="Mango Milk Tea" price="7.99" description="Mango Milk tea with tapioca pearls" image="./bubble-tea-test.jpg" />
      </div>
    </div>
  )
}

export default App
