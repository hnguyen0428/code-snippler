import {theme} from '../../../constants/GlobalStyles';


export const styles = {
    rootCtn: {
        display: 'flex',
        justifyContent: 'center',
        width: '100%',
        backgroundColor: theme.backgroundColor
    },
    contentCtn: {
        width: '800px',
        display: 'flex',
        flexDirection: 'column',
        marginTop: '20px',
    },
    snippetsCtn: {
        width: '100%',
    },
    settingsIcon: {
        alignSelf: 'flex-end'
    },
    emptyContentCtn: {
        width: '800px',
        display: 'flex',
        flexDirection: 'column',
        marginTop: '20px'
    },
    emptySnippetMessageCtn: {
        height: '700px',
        display: 'flex',
        flexDirection: 'column',
        justifyContent: 'center',
        alignItems: 'center'
    },
    noSnippetMessage: {
        fontSize: '50px',
        textAlign: 'center'
    },
};