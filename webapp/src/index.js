import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './root/App';
import registerServiceWorker from './registerServiceWorker';
import store from './redux/store';
import {LOGIN} from './redux/actions/types';
import Snippler from './api/SnipplerClient';
import {handleActionsResult} from './redux/actions/actions';
import {Provider} from 'react-redux';
import './assets/react-toolbox/theme.css';
import theme from './assets/react-toolbox/theme';
import ThemeProvider from 'react-toolbox/lib/ThemeProvider';


// Persisting user
const apiKey = localStorage.getItem('apiKey');

if (apiKey) {
    const userData = localStorage.getItem(apiKey);
    if (userData) {
        let user = JSON.parse(userData);

        // Inject api key into user object
        user.apiKey = apiKey;
        store.dispatch({type: LOGIN, payload: user});
    }
    else {
        Snippler.user().getMe(null, (res, error) => {
            let success = handleActionsResult(res, error, null, null);

            if (success) {
                let user = res.data;
                user.apiKey = apiKey;
                store.dispatch({type: LOGIN, payload: user});
            }
        });
    }
}


ReactDOM.render((
    <Provider store={store}>
        <ThemeProvider theme={theme}>
            <App />
        </ThemeProvider>
    </Provider>
    ), document.getElementById('root'));
registerServiceWorker();
