import {SEARCH_SNIPPETS} from '../actions/types';


const initialState = {
    snippets: {
        values: [],
        query: ''
    },
};

export default function(state = initialState, action) {
    switch (action.type) {
        case SEARCH_SNIPPETS:
            return {
                ...state,
                snippets: {
                    ...state.snippets,
                    values: action.payload.values,
                    query: action.payload.query
                }
            };
        default:
            return state
    }
}