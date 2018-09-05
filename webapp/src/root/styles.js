import {theme} from '../constants/GlobalStyles';


export const textColor = '#E1E1E1';

export const styles = {
    app: {
        fontFamily: 'Lucida Grande',
        fontSize: '25px',
        backgroundColor: theme.backgroundColor,
        height: '100vh',
        width: '100vw',
        minHeight: '100vh',
        minWidth: '100vw',
        color: '#213838',
    },
    navBar: {
        backgroundColor: '#313133',
        height: '45px',
        flexDirection: 'row',
        justifyContent: 'center'
    },
    navItemsCtn: {
        width: '1000px',
        display: 'flex',
        flexDirection: 'row'
    },
    navBarButtonsCtn: {
        display: 'flex',
        justifyContent: 'left',
        flexGrow: '2',
    },
    navBarTitle: {
        fontFamily: 'Courier New Lucida Console',
        fontSize: '20px',
        fontWeight: '600',
        color: textColor
    },
    filterDrawer: {
        backgroundColor: '#313133',
        height: 'auto'
    },
    filterHelpText: {
        maxWidth: '200px',
        color: 'white'
    },
    searchBar: {
        flex: '1'
    },
    codeIcon: {
        marginRight: '5px',
        marginLeft: '5px'
    },
    loginButtonCtn: {
        display: 'flex',
        justifyContent: 'flex-end',
        flexGrow: '3'
    },
    loginButton: {
        fontFamily: 'inherit',
        fontSize: '18px'
    },
    iconColor: {
        color: textColor
    },
    usernameMenuItem: {
        backgroundColor: '#F9F9F9'
    },
};