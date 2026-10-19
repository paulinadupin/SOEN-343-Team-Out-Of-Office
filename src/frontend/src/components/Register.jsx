import './Register.css'

function Register() {
    return (
        // TODO: Change register form depending on account type (customer, restaurant, courier, operator)
        <div className="register">
            <h1>Register</h1>
            <label>Name</label>
            <input type="text" placeholder="Name"></input>
            <label>Username</label>
            <input type="text" placeholder="Username"></input>
            <label>Password</label>
            <input type="password" placeholder="Password"></input>
            <label>Delivery Address</label>
            <input type="text" placeholder="Delivery Address"></input>
            <button onClick={() => {}}>Register</button>
            <a href="/login">Already have an account? Login</a>
        </div>
    )
}

export default Register;