import './RestaurantHeader.css';

function RestaurantHeader({name, address, status}) {
  return (
    <div className="restaurant-header">
      <h1 className="title">{name}</h1>
      <p className="address">{address}</p>
      <div className={`status ${status}`}>
        <svg width="19" height="19">
          <circle cx="8" cy="8" r="6"/>
        </svg>
        {status}
        </div>
    </div>
  );
}

export default RestaurantHeader;