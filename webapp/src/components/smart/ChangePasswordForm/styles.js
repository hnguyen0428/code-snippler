import {theme} from "../../../constants/GlobalStyles";

export const styles = {
    dialogCtn: {
        display: 'flex',
        flexDirection: 'column',
        backgroundColor: theme.backgroundColor,
        justifyContent: 'center'
    },
    saveBtn: {
        alignSelf: 'flex-end'
    },
    input: {
        width: 400,
        marginTop: 10,
        marginBottom: 10,
        alignSelf: 'center'
    },
    adornment: {
        color: '#6A6A6A'
    }
};