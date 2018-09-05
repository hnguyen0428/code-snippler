import {SETTINGS_MOST_POPULAR, SETTINGS_MOST_SAVED, SETTINGS_MOST_VIEWS, SETTINGS_MOST_UPVOTED,
    SETTINGS_MOST_RECENT} from '../actions/types';
import FeedSettings from '../../constants/FeedSettings';


const feedSettings = Number(localStorage.getItem('feedSettings'));

const initialState = {
    feedSettings: feedSettings ? feedSettings : FeedSettings.MOST_POPULAR
};

export default function(state = initialState, action) {
    switch (action.type) {
        case SETTINGS_MOST_POPULAR:
            localStorage.setItem('feedSettings', FeedSettings.MOST_POPULAR);
            return {
                ...state,
                feedSettings: FeedSettings.MOST_POPULAR
            };
        case SETTINGS_MOST_VIEWS:
            localStorage.setItem('feedSettings', FeedSettings.MOST_VIEWS);
            return {
                ...state,
                feedSettings: FeedSettings.MOST_VIEWS
            };
        case SETTINGS_MOST_UPVOTED:
            localStorage.setItem('feedSettings', FeedSettings.MOST_UPVOTED);
            return {
                ...state,
                feedSettings: FeedSettings.MOST_UPVOTED
            };
        case SETTINGS_MOST_SAVED:
            localStorage.setItem('feedSettings', FeedSettings.MOST_SAVED);
            return {
                ...state,
                feedSettings: FeedSettings.MOST_SAVED
            };
        case SETTINGS_MOST_RECENT:
            localStorage.setItem('feedSettings', FeedSettings.MOST_RECENT);
            return {
                ...state,
                feedSettings: FeedSettings.MOST_RECENT
            };
        default:
            return state
    }
}