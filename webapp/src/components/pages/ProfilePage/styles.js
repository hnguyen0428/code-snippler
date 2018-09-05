import {theme} from "../../../constants/GlobalStyles";

export const styles = {
    rootCtn: {
        marginTop: '20px',
        flexDirection: 'row',
        display: 'flex',
        justifyContent: 'center',
        position: 'absolute',
        width: '100%',
        height: 'auto',
        backgroundColor: theme.backgroundColor,
    },
    profileInfoDialogCtn: {
        backgroundColor: theme.backgroundColor
    },
    contentCtn: {
        width: '800px',
        marginBottom: '50px'
    },
    usernameCtn: {
        height: '40px',
        marginBottom: '40px'
    },
    profileIcon: {
        marginRight: '10px'
    },
    username: {
        height: '100%',
        whiteSpace: 'pre-line',
        lineHeight: '1.4em',
        fontSize: '15px',
        cursor: 'pointer'
    },
    actionsCtn: {
        flexDirection: 'row',
        display: 'flex'
    },
    snippetsCtn: {
        width: '100%'
    },
    searchBar: {
        marginTop: '10px',
    }
};

export const materialStyles = theme => ({
    indicator: {
        backgroundColor: '#494949',
    },
    tabLabel: {
        color: '#494949'
    }
});