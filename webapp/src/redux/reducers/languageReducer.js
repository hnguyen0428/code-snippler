import {FETCH_ALL_LANGUAGES} from '../actions/types';


const initialState = {
    languages: []
};


export default function(state = initialState, action) {
    switch (action.type) {
        case FETCH_ALL_LANGUAGES: {
            let byNames = {};
            let languages = action.payload;
            languages.forEach(language => {
                byNames[language.name] = language;
            });

            return {
                ...state,
                byNames: byNames
            };
        }
        default:
            return state;
    }
}