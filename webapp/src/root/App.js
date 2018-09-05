import React, { Component } from 'react';
import {connect} from 'react-redux';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import ClickAwayListener from '@material-ui/core/ClickAwayListener';
import Grow from '@material-ui/core/Grow';
import Paper from '@material-ui/core/Paper';
import Popper from '@material-ui/core/Popper';
import MenuItem from '@material-ui/core/MenuItem';
import MenuList from '@material-ui/core/MenuList';
import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import Tooltip from '@material-ui/core/Tooltip';
import Drawer from '@material-ui/core/Drawer';
import InputLabel from '@material-ui/core/InputLabel';

import SearchBar from '../components/dumb/SearchBar/SearchBar';
import SelectableChip from '../components/dumb/SelectableChip/SelectableChip';

import AccountCircle from '@material-ui/icons/AccountCircle';
import FilterList from '@material-ui/icons/FilterList';
import RadioButtonChecked from '@material-ui/icons/RadioButtonChecked';
import RadioButtonUnchecked from '@material-ui/icons/RadioButtonUnchecked';
import Add from '@material-ui/icons/Add';


import {Router, Route, Switch} from 'react-router-dom';
import history from './history'

import {logout} from "../redux/actions/authActions";
import {closeAlert, showBinaryAlert, closeBinaryAlert} from "../redux/actions/alertActions";
import {overridePath} from "../redux/actions/routerActions";
import {searchSnippets} from "../redux/actions/snippetActions";

import SnippetFormPage from '../components/pages/SnippetFormPage/SnippetFormPage';
import LoginPage from '../components/pages/LoginPage/LoginPage';
import SnippetDetailsPage from '../components/pages/SnippetDetailsPage/SnippetDetailsPage';
import HomePage from '../components/pages/HomePage/HomePage';
import SearchPage from '../components/pages/SearchPage/SearchPage';
import ProfilePage from '../components/pages/ProfilePage/ProfilePage';

import SnipplerConfig from '../constants/SnipplerConfig';
import {supportedLanguages} from '../constants/languages';
import Snippler from '../api/SnipplerClient';

import {styles, textColor} from './styles';


class App extends Component {
    constructor(props) {
        super(props);

        const languagesFilter = {};
        supportedLanguages.map(language => {languagesFilter[language] = false});

        let languages = Snippler.getLanguagesFilter();

        if (Snippler.getLanguagesFilter().length === 0)
            languagesFilter["All"] = true;
        else
            languages.forEach(language => {languagesFilter[language] = true});

        this.state = {
            open: false,
            filterDrawerOpen: false,
            searchQuery: '',
            languagesFilter: languagesFilter
        }
    }


    handleToggle = () => {
        this.setState(state => ({ open: !this.state.open }));
    };


    toggleFilterDrawer = (open) => {
        if (open !== undefined)
            this.setState({filterDrawerOpen: open});
        else
            this.setState({filterDrawerOpen: !this.state.filterDrawerOpen});
    };


    redirectToLogin = () => {
        history.push('/login');
        this.props.closeBinaryAlert();
    };


    handleAddSnippet = () => {
        if (!this.props.auth.loggedIn) {
            let actionOne = {title: 'Dismiss'};
            let actionTwo = {title: 'Sign In', callback: () => {
                this.redirectToLogin();
                this.props.overridePath('/snippet');
            }};
            if (!this.props.auth.loggedIn)
                this.props.showBinaryAlert('Sign In?', 'To create a snippet, you must sign in', actionOne, actionTwo);
        }
        else {
            history.push('/snippet');
        }
    };


    handleClickFilterButton = () => {
        this.setState({filterDrawerOpen: !this.state.filterDrawerOpen});
    };


    onClickFilterChip = (chip, checked) => {
        if (chip.props.name === "All" && checked) {
            const languagesFilter = {};
            supportedLanguages.map(language => {
                languagesFilter[language] = false
            });
            Snippler.resetLanguagesFilter();
            languagesFilter["All"] = true;

            this.setState({
                languagesFilter: languagesFilter
            });
        }
        else {
            this.setState({
                languagesFilter: {
                    ...this.state.languagesFilter,
                    All: false,
                    [chip.props.name]: checked
                }
            });
            if (checked)
                Snippler.addToLanguagesFilter(chip.props.name);
            else
                Snippler.removeFromLanguagesFilter(chip.props.name);
        }
    };


    handleClose = event => {
        if (this.anchorEl.contains(event.target)) {
            return;
        }

        this.setState({ open: false });
    };


    handleProfileClick = event => {
        this.setState({ open: false });
        history.push('/user/me');
    };


    handleLogout = event => {
        this.setState({ open: false });
        this.props.logout();
        window.location.reload();
    };


    handleSignIn = event => {
        this.setState({ open: false });
        history.push('/login');
    };


    closeDialog = (event) => {
        this.props.closeAlert();
    };


    handleSearchBarEdit = (event) => {
        this.setState({searchQuery: event.target.value});
    };


    handleSearch = (query) => {
        if (query.length !== 0) {
            let params = {
                page: 0,
                pageSize: SnipplerConfig.SEARCH_PAGE_SIZE,
                query: query
            };

            this.props.searchSnippets(params);

            if (this.props.router.path !== '/search') {
                history.push('/search');
            }
        }
    };


    render() {
        const { open } = this.state;

        return (
            <div style={styles.app}>
                <Drawer anchor="right" open={this.state.filterDrawerOpen}
                        onClose={() => this.toggleFilterDrawer(false)}
                >
                    <div style={styles.filterDrawer}>
                        <SelectableChip
                            name="All"
                            label="All"
                            checked={this.state.languagesFilter["All"]}
                            onClick={this.onClickFilterChip}
                            checkedIcon={<RadioButtonChecked/>}
                            icon={<RadioButtonUnchecked/>}
                        />
                        {supportedLanguages.map(language => {
                            return (
                                <SelectableChip
                                    name={language}
                                    label={language}
                                    checked={this.state.languagesFilter[language]}
                                    onClick={this.onClickFilterChip}
                                />
                            );
                        })}
                        <Toolbar>
                            <InputLabel style={styles.filterHelpText}>
                                You must refresh the page for the filters to take effects
                            </InputLabel>
                        </Toolbar>
                    </div>
                </Drawer>

                <Dialog id="General Alert"
                        open={this.props.alert.generalAlert.active}
                        onBackdropClick={this.closeDialog}
                        onEscapeKeyDown={this.closeDialog}>
                    { this.props.alert.generalAlert.title && (
                        <DialogTitle>{this.props.alert.generalAlert.title}</DialogTitle>
                    )}
                    <DialogContent>
                        <DialogContentText style={{whiteSpace: 'pre-line'}}>
                            {this.props.alert.generalAlert.message}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.closeDialog} color="primary">
                            Close
                        </Button>
                    </DialogActions>
                </Dialog>

                <Dialog id="Binary Alert"
                        open={this.props.alert.binaryAlert.active}
                        onBackdropClick={this.closeDialog}
                        onEscapeKeyDown={this.closeDialog}>
                    { this.props.alert.binaryAlert.title && (
                        <DialogTitle>{this.props.alert.binaryAlert.title}</DialogTitle>
                    )}
                    <DialogContent>
                        <DialogContentText style={{whiteSpace: 'pre-line'}}>
                            {this.props.alert.binaryAlert.message}
                        </DialogContentText>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={this.props.alert.binaryAlert.actionOne} color="primary">
                            {this.props.alert.binaryAlert.actionOneTitle}
                        </Button>
                        <Button onClick={this.props.alert.binaryAlert.actionTwo} color="primary">
                            {this.props.alert.binaryAlert.actionTwoTitle}
                        </Button>
                    </DialogActions>
                </Dialog>

                <AppBar style={styles.navBar}>
                    <div style={styles.navItemsCtn}>
                        <div style={styles.navBarButtonsCtn}>
                            <Button href="/" style={styles.navBarTitle}>
                                Code Snippler
                            </Button>
                            <SearchBar
                                style={styles.searchBar}
                                onClickSearch={this.handleSearch}
                                onChange={this.handleSearchBarEdit}
                                value={this.state.searchQuery}
                            />
                        </div>

                        <div style={styles.loginButtonCtn}>
                            <Tooltip disableFocusListener disableTouchListener title="Filter by Languages">
                                <IconButton
                                    onClick={this.handleClickFilterButton}
                                >
                                    <FilterList style={styles.iconColor}/>
                                </IconButton>
                            </Tooltip>

                            <Tooltip disableFocusListener disableTouchListener title="Add a Snippet">
                                <IconButton
                                    onClick={this.handleAddSnippet}
                                >
                                    <Add style={styles.iconColor}/>
                                </IconButton>
                            </Tooltip>

                            <IconButton
                                onClick={this.handleToggle}
                                buttonRef={node => {this.anchorEl = node;}}
                            >
                                <AccountCircle style={styles.iconColor}/>
                            </IconButton>

                            <Popper open={open} anchorEl={this.anchorEl} transition disablePortal>
                                {({ TransitionProps, placement }) => (
                                    <Grow
                                        {...TransitionProps}
                                        style={{ transformOrigin: placement === 'bottom' ? 'center top' : 'center bottom' }}
                                    >
                                        <Paper>
                                            <ClickAwayListener onClickAway={this.handleClose}>
                                                {this.props.auth.loggedIn ? (
                                                    <MenuList>
                                                        <MenuItem style={styles.usernameMenuItem}
                                                                  onClick={this.handleProfileClick}>
                                                            {this.props.auth.currentUser.username}
                                                        </MenuItem>
                                                        <MenuItem onClick={this.handleLogout}>Logout</MenuItem>
                                                    </MenuList>
                                                ) : (
                                                    <MenuList>
                                                        <MenuItem onClick={this.handleSignIn}>Sign In</MenuItem>
                                                    </MenuList>
                                                )}
                                            </ClickAwayListener>
                                        </Paper>
                                    </Grow>
                                )}
                            </Popper>
                        </div>
                    </div>
                </AppBar>
                <div style={{height: '45px'}}/>
                <Router history={history}>
                    <Switch>
                        <Route exact path="/" component={HomePage}/>
                        <Route exact path="/snippet" component={SnippetFormPage}/>
                        <Route exact path="/login" component={LoginPage}/>
                        <Route exact path="/snippet/:snippetId" component={SnippetDetailsPage}/>
                        <Route exact path="/search" component={SearchPage}/>
                        <Route exact path="/user/:userId" component={ProfilePage}/>
                    </Switch>
                </Router>
            </div>
        );
    }
}


function mapStateToProps(state) {
    return {
        auth: state.auth,
        alert: state.alert,
        router: state.router
    }
}


export default connect(mapStateToProps, {
    logout,
    showBinaryAlert,
    closeBinaryAlert,
    overridePath,
    closeAlert,
    searchSnippets
})(App);