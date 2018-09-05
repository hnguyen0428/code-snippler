import {ALERT_ACTIVE, ALERT_INACTIVE, BINARY_ALERT_ACTIVE, BINARY_ALERT_INACTIVE} from '../actions/types';


const initialState = {
    generalAlert: {
        title: null,
        message: null,
        active: false
    },
    binaryAlert: {
        title: null,
        message: null,
        active: false,
        actionOne: null,
        actionTwo: null,
        actionOneTitle: null,
        actionTwoTitle: null
    }
};


export default function(state = initialState, action) {
    switch (action.type) {
        case ALERT_ACTIVE:
            return {
                ...state,
                generalAlert: {
                    title: action.payload.title,
                    message: action.payload.message,
                    active: true
                }
            };
        case ALERT_INACTIVE:
            return {
                ...state,
                generalAlert: initialState.generalAlert
            };
        case BINARY_ALERT_ACTIVE:
            return {
                ...state,
                binaryAlert: {
                    title: action.payload.title,
                    message: action.payload.message,
                    active: true,
                    actionOne: action.payload.actionOne,
                    actionTwo: action.payload.actionTwo,
                    actionOneTitle: action.payload.actionOneTitle,
                    actionTwoTitle: action.payload.actionTwoTitle,
                }
            };
        case BINARY_ALERT_INACTIVE:
            return {
                ...state,
                binaryAlert: initialState.binaryAlert
            };
        default:
            return state;
    }
}