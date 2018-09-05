import {LOCATION_CHANGE} from 'react-router-redux';
import {OVERRIDE_PATH, RESET_OVERRIDE_PATH} from '../actions/types';

const initialState = {
    prevPath: '/',
    path: '/',
    overridePath: null
};

export default function(state = initialState, action) {
    switch (action.type) {
        case LOCATION_CHANGE:
            let prevPath = state.path;

            return {
                ...state,
                path: action.payload.pathname,
                prevPath: prevPath
            };
        case OVERRIDE_PATH: // Define the path that login page will go to after logging in
            return {
                ...state,
                overridePath: action.payload.pathname
            };
        case RESET_OVERRIDE_PATH:
            return {
                ...state,
                overridePath: null
            };
        default:
            return state
    }
}