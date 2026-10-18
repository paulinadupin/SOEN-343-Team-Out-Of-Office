

export const navConfig = {
    [ROLES.CUSTOMER]: {
        sidebar: [
            {label: 'Home', path: '/home'},
            {label: 'Browse', path: '/browse'},
            {label: 'Orders', path: '/orders'},
        ]
    },
    [ROLES.RESTAURANT]: {
        sidebar: [
            {label: 'Dashboard', path: '/dashboard'},
            {label: 'Menu', path: '/menu'},
            {label: 'Orders', path: '/orders'},
        ]
    }
}