import React, { Component } from 'react';
import {connect} from 'react-redux';
import {withRouter} from 'react-router-dom';

import history from '../../../root/history';

import Card from '@material-ui/core/Card';
import Input from '@material-ui/core/Input';
import InputLabel from '@material-ui/core/InputLabel';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import InputAdornment from '@material-ui/core/InputAdornment';
import IconButton from '@material-ui/core/IconButton';
import Lock from '@material-ui/icons/Lock';
import PermIdentity from '@material-ui/icons/PermIdentity';
import CheckCircle from '@material-ui/icons/CheckCircle';

import {styles} from './styles';

import {login, register} from '../../../redux/actions/authActions';
import {showAlert, closeAlert} from '../../../redux/actions/alertActions';
import {resetOverridePath} from '../../../redux/actions/routerActions';
import Utility from "../../../util/Utility";


class LoginPage extends Component {
    PASSWORD_INPUT_LABEL = 'Password must be alphanumeric and contains between 6-20 characters';
    USERNAME_INPUT_LABEL = 'Username must contain only the characters [a-z, A-Z, 0-9, _] and be ' +
        'between 6-20 characters';
    REGISTER_BUTTON_LABEL = 'Become a Code Snippler';
    LOGIN_BUTTON_LABEL = 'Login';

    USERNAME_CHAR_INVALID_ERR = 'Username must contain only [a-zA-Z0-9_].';
    USERNAME_LENGTH_ERR = 'Username must be between 6 to 20 characters.';
    PASSWORD_CHAR_INVALID_ERR = 'Password must be alphanumeric.';
    PASSWORD_LENGTH_ERR = 'Password must be between 6 to 20 characters.';
    CONF_PW_NOT_MATCHED = 'Confirmation password does not match password';

    WELCOME_MSG = 'Welcome';
    REDIRECT_MSG = 'We will redirect you to the homepage in a bit...';


    constructor(props) {
        super(props);

        this.state = {
            dialogOpen: false,
            errorDialogOpen: false,
            welcomeDialogOpen: false,
            registerUsername: '',
            registerPassword: '',
            registerConfirmPassword: '',
            loginUsername: '',
            loginPassword: '',
            errorMessages: []
        };

        this.onClickRegister = this.onClickRegister.bind(this);
        this.closeRegisterDialog = this.closeRegisterDialog.bind(this);
        this.submitRegister = this.submitRegister.bind(this);
        this.submitLogin = this.submitLogin.bind(this);
    }


    closeRegisterDialog() {
        this.setState({
            dialogOpen: false
        });
    }


    onClickRegister() {
        this.setState({
            dialogOpen: true
        });
    }


    componentWillMount() {
        if (this.props.auth.loggedIn)
            history.push('/');
    }


    submitRegister() {
        let errors = this.registerInputSanityCheck();

        if (errors.length !== 0) {
            this.props.showAlert('Error', errors.join('\n'));
        }
        else {
            this.props.register(this.state.registerUsername, this.state.registerPassword,
                (res, err) => {
                    if (res) {
                        this.props.showAlert(this.WELCOME_MSG, this.REDIRECT_MSG);
                        setInterval(() => {
                            this.props.closeAlert();
                            history.push('/');
                            window.location.reload();
                        }, 2000);
                    }
                });
        }
    }


    submitLogin() {
        this.props.login(this.state.loginUsername, this.state.loginPassword,
            (res, err) => {
                if (res) {
                    let prevPath = this.props.router.prevPath;
                    let overridePath = this.props.router.overridePath;
                    if (overridePath)
                        history.push(overridePath);
                    else
                        history.push(prevPath);

                    this.props.resetOverridePath();

                    // Reload to fetch information about feed
                    window.location.reload();
                }
            });
    }


    registerInputSanityCheck() {
        let errors = [];
        let regexTest = Utility.validateUsername(this.state.registerUsername);
        if (!regexTest)
            errors.push(this.USERNAME_CHAR_INVALID_ERR);

        let rangeTest = Utility.withinRange(this.state.registerUsername, 6, 20);
        if (!rangeTest)
            errors.push(this.USERNAME_LENGTH_ERR);

        regexTest = Utility.isAlphanum(this.state.registerPassword);
        if (!regexTest)
            errors.push(this.PASSWORD_CHAR_INVALID_ERR);

        rangeTest = Utility.withinRange(this.state.registerPassword, 6, 20);
        if (!rangeTest)
            errors.push(this.PASSWORD_LENGTH_ERR);

        if (errors.length === 0 && this.state.registerPassword !== this.state.registerConfirmPassword)
            errors.push(this.CONF_PW_NOT_MATCHED);

        return errors;
    }


    onInputEdit = (event) => {
        this.setState({
            [event.target.name]: event.target.value
        });
    };


    render() {
        return (
            <div style={styles.rootCtn}>
                <Dialog
                    open={this.state.dialogOpen} onBackdropClick={this.closeRegisterDialog}
                    onEscapeKeyDown={this.closeRegisterDialog}
                    maxWidth="md"
                >
                    <div style={styles.registerDialog}>
                        <div style={styles.formCtn}>
                            <div style={styles.inputCtn}>
                                <Input style={styles.input} placeholder="Username" startAdornment={
                                    <InputAdornment style={styles.adornment} position="start">
                                        <PermIdentity/>
                                    </InputAdornment>
                                }
                                       value={this.state.registerUsername}
                                       onChange={this.onInputEdit}
                                       name="registerUsername"
                                />
                                <InputLabel style={styles.inputLabel}>
                                    {this.USERNAME_INPUT_LABEL}
                                </InputLabel>
                            </div>
                            <div style={styles.inputCtn}>
                                <Input style={styles.input} placeholder="Password" type="password"
                                       startAdornment={
                                           <InputAdornment style={styles.adornment} position="start">
                                               <Lock/>
                                           </InputAdornment>
                                       }
                                       value={this.state.registerPassword}
                                       onChange={this.onInputEdit}
                                       name="registerPassword"
                                />
                                <InputLabel style={styles.inputLabel}>
                                    {this.PASSWORD_INPUT_LABEL}
                                </InputLabel>
                            </div>
                            <div style={styles.inputCtn}>
                                <Input style={styles.input} placeholder="Confirm Password" type="password"
                                       startAdornment={
                                           <InputAdornment style={styles.adornment} position="start">
                                               <CheckCircle/>
                                           </InputAdornment>
                                       }
                                       value={this.state.registerConfirmPassword}
                                       onChange={this.onInputEdit}
                                       name="registerConfirmPassword"
                                />
                            </div>
                            <div style={styles.registerBtnCtn}>
                                <Button color="primary" variant="raised" onClick={this.submitRegister}
                                        fullWidth>
                                    {this.REGISTER_BUTTON_LABEL}
                                </Button>
                            </div>
                        </div>
                    </div>
                </Dialog>


                <Card style={styles.card}>
                    <div style={styles.formCtn}>
                        <div style={styles.inputCtn}>
                            <Input style={styles.input} placeholder="Username" startAdornment={
                                <InputAdornment style={styles.adornment} position="start">
                                    <PermIdentity/>
                                </InputAdornment>
                            }
                                   value={this.state.loginUsername}
                                   onChange={this.onInputEdit}
                                   name="loginUsername"
                            />
                        </div>
                        <div style={styles.inputCtn}>
                            <Input style={styles.input} placeholder="Password" type="password" startAdornment={
                                <InputAdornment style={styles.adornment} position="start">
                                    <Lock/>
                                </InputAdornment>
                            }
                                   value={this.state.loginPassword}
                                   onChange={this.onInputEdit}
                                   name="loginPassword"
                            />
                        </div>
                        <div style={styles.buttonCtn}>
                            <Button
                                color="primary"
                                variant="raised"
                                onClick={this.submitLogin}
                                disabled={this.state.loginUsername.length === 0 ||
                                this.state.loginPassword.length === 0}
                            >
                                {this.LOGIN_BUTTON_LABEL}
                            </Button>
                        </div>

                        <h3 style={styles.registerText}>
                            Don't have an account? {}
                            <a href="javascript:void(0)" onClick={this.onClickRegister}>Register</a>
                        </h3>
                    </div>
                </Card>
            </div>
        );
    }
}


function mapStateToProps(state) {
    return {
        auth: state.auth,
        router: state.router
    };
}


export default withRouter(connect(mapStateToProps, {
    login,
    register,
    showAlert,
    closeAlert,
    resetOverridePath
})(LoginPage));