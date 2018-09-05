import {theme} from '../../../constants/GlobalStyles';


export const styles = {
    rootCtn: {
        marginTop: '20px',
        flexDirection: 'row',
        display: 'flex',
        justifyContent: 'center',
        position: 'absolute',
        width: '100%',
        height: 'auto',
        backgroundColor: theme.backgroundColor
    },
    contentCtn: {
        width: '800px',
        marginBottom: '50px'
    },
    actionsCtn: {
        flexDirection: 'row',
        display: 'flex'
    },
    usernameCtn: {
        height: '40px'
    },
    profileIcon: {
        marginRight: '5px'
    },
    username: {
        cursor: 'pointer',
        height: '100%'
    },
    dateCtn: {
        display: 'flex',
        flexDirection: 'column'
    },
    dateLabel: {
        fontSize: '12px',
        whiteSpace: 'pre-line',
        lineHeight: '1.5em',
    },
    metadataCtn: {

    },
    header: {
        fontSize: '34px',
        fontFamily: 'Lucida Grande, Sans Serif',
        fontWeight: '300',
    },
    descriptionCtn: {
        width: '85%',
        paddingLeft: '40px',
        marginTop: '15px',
        marginBottom: '30px'
    },
    description: {
        fontSize: '15px',
        fontFamily: 'Lucida Grande, Sans Serif',
        whiteSpace: 'pre-line',
    },
    editor: {
        height: '650px',
        width: 'inherit',
        marginBottom: '10px'
    },
    commentsListCtn: {
        display: 'flex',
        flexDirection: 'column'
    },
    commentsSettingsIcon: {
        alignSelf: 'flex-end'
    },
    commentsList: {
        maxHeight: '800px',
        overflow: 'auto',
        backgroundColor: 'white',
        boxShadow: '0px 0px 10px -3px #999',
    },
    commentTextField: {
        marginTop: '20px',
        marginBottom: '20px'
    },
    commentBoxAvatar: {
        marginRight: '10px',
        marginBottom: '5px'
    },
    commentBoxBtn: {
        alignSelf: 'flex-end'
    },
    iconsCtn: {
        display: 'flex',
        flexDirection: 'row',
        width: '100%',
        height: '20px',
        justifyContent: 'flex-end',
        paddingBottom: '20px',
        marginBottom: '50px'
    },
    iconCtn: {
        fontSize: '18px',
        marginLeft: '3px',
        marginRight: '3px',
    },
    icon: {
        width: '23px',
        height: '23px',
        marginRight: '3px',
    }
};


export const materialStyles = {
    label: {
        color: '#595959'
    }
};