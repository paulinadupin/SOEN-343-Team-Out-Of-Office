import './MenuItem.css'

function MenuItem({ name, price, description, image}) {
    return (
        <div className="menu-item">
            <img src={image} alt={name} className="img"></img>
            <div className="content">
                <h2 className="title">{name}</h2>
                <p className="description">{description}</p>
                <p className="price">${price}</p>
            </div>
            <button className="add-cart">+</button>
        </div>
    )
}

export default MenuItem