import {FETCH_ME, FETCH_USER, FETCH_USERS} from '../actions/types';


const initialState = {
    byIds: {}
};


export default function(state = initialState, action) {
    switch (action.type) {
        case FETCH_USER:
        case FETCH_ME: {
            return {
                ...state,
                byIds: {
                    ...state.byIds,
                    [action.payload.userId]: action.payload
                }
            };
        }
        case FETCH_USERS: {
            let users = action.payload;
            let byIds = state.byIds;

            users.forEach(user => {
                byIds[user.userId] = user;
            });
            return {
                ...state,
                byIds: byIds
            };
        }
        default:
            return state;
    }
}