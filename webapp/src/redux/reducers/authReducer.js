import {LOGIN, REGISTER, LOGGED_OUT, FETCH_ME} from '../actions/types';
import Snippler from '../../api/SnipplerClient';


const initialState = {
    currentUser: null,
    loggedIn: false
};


export default function(state = initialState, action) {
    switch (action.type) {
        case LOGIN:
        case REGISTER: {
            // Save apiKey and the user data to persist user
            localStorage.setItem('apiKey', action.payload.apiKey);
            localStorage.setItem(action.payload.apiKey, JSON.stringify(action.payload));

            Snippler.setApiKey(action.payload.apiKey);

            return {
                ...state,
                currentUser: action.payload,
                loggedIn: true
            };
        }
        case LOGGED_OUT:
            return {
                ...state,
                currentUser: null,
                loggedIn: false
            };
        case FETCH_ME:
            return {
                ...state,
                currentUser: {
                    ...state.currentUser,
                    ...action.payload
                }
            };
        default:
            return state;
    }
}