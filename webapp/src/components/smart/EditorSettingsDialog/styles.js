import {theme} from '../../../constants/GlobalStyles';


export const styles = {
    dialogCtn: {
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: theme.backgroundColor
    },
    contentCtn: {
        display: 'flex',
        flexDirection: 'row',
        maxHeight: 600,
        height: 440
    },
    selectors: {
        maxHeight: 500,
        height: 400,
        flex: 2,
        position: 'relative',
        overflow: 'auto'
    },
    aceEditor: {
        maxHeight: 500,
        height: 418,
        flex: 5
    },
    saveBtn: {
        alignSelf: 'flex-end'
    }
};