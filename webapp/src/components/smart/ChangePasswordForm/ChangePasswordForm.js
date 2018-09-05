import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogContentText from '@material-ui/core/DialogContentText';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Toolbar from '@material-ui/core/Toolbar';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import InputLabel from '@material-ui/core/InputLabel';
import Input from '@material-ui/core/Input';
import InputAdornment from '@material-ui/core/InputAdornment';
import TextField from '@material-ui/core/TextField';

import Lock from '@material-ui/icons/Lock';
import CheckCircle from '@material-ui/icons/CheckCircle';
import ArrowBackIos from '@material-ui/icons/ArrowBackIos';

import {changePassword} from "../../../redux/actions/authActions";
import {showAlert, closeAlert} from "../../../redux/actions/alertActions";

import SnipplerConfig from '../../../constants/SnipplerConfig';

import {styles} from './styles';
import Utility from "../../../util/Utility";


class ChangePasswordForm extends Component {
    PASSWORD_CHAR_INVALID_ERR = 'Password must be alphanumeric.';
    PASSWORD_LENGTH_ERR = 'Password must be between 6 to 20 characters.';
    CONF_PW_NOT_MATCHED = 'Confirmation password does not match new password';


    constructor(props) {
        super(props);

        this.state = {
            currPw: '',
            newPw: '',
            confirmPw: ''
        }
    }


    sanityCheck = () => {
        let errors = [];

        let regexTest = Utility.isAlphanum(this.state.newPw);
        if (!regexTest) {
            errors.push(this.PASSWORD_CHAR_INVALID_ERR);
        }

        let rangeTest = Utility.withinRange(this.state.newPw, 6, 20);
        if (!rangeTest) {
            errors.push(this.PASSWORD_LENGTH_ERR);
        }

        if (this.state.newPw !== this.state.confirmPw) {
            errors.push(this.CONF_PW_NOT_MATCHED);
        }

        if (errors.length !== 0)
            this.props.showAlert("Error", errors.join("\n"));

        return errors.length === 0;
    };


    onInputEdit = (event) => {
        this.setState({[event.target.name]: event.target.value});
    };


    onClickSave = (event) => {
        let passed = this.sanityCheck();

        if (!passed) {
            this.props.onClickSave(this, passed);
            return;
        }

        this.props.changePassword(this.state.currPw, this.state.newPw, (res, err) => {
            let success = false;
            if (res && res.success) {
                success = true;
            }
            else if (err) {
                success = false;
            }

            if (this.props.onClickSave)
                this.props.onClickSave(this, success);
        });
    };


    onClickBack = () => {
        if (this.props.onClickBack)
            this.props.onClickBack(this);
    };


    onBackdropClick = () => {
        if (this.props.onBackdropClick)
            this.props.onBackdropClick(this);
    };


    render() {
        const {onBackdropClick, ...props} = this.props;

        return (
            <Dialog {...props} onBackdropClick={this.onBackdropClick}>
                <DialogContent style={styles.dialogCtn}>
                    {this.props.withBackButton &&
                    <IconButton onClick={this.onClickBack}>
                        <ArrowBackIos/>
                    </IconButton>
                    }

                    <DialogTitle>Change Password</DialogTitle>

                    <Input style={styles.input} placeholder="Current Password" type="password"
                           startAdornment={
                               <InputAdornment style={styles.adornment} position="start">
                                   <Lock/>
                               </InputAdornment>
                           }
                           value={this.state.currPw}
                           onChange={this.onInputEdit}
                           name="currPw"
                    />

                    <br/>

                    <Input style={styles.input} placeholder="New Password" type="password"
                           startAdornment={
                               <InputAdornment style={styles.adornment} position="start">
                                   <Lock/>
                               </InputAdornment>
                           }
                           value={this.state.newPw}
                           onChange={this.onInputEdit}
                           name="newPw"
                    />

                    <br/>

                    <Input style={styles.input} placeholder="Confirm Password" type="password"
                           startAdornment={
                               <InputAdornment style={styles.adornment} position="start">
                                   <CheckCircle/>
                               </InputAdornment>
                           }
                           value={this.state.confirmPw}
                           onChange={this.onInputEdit}
                           name="confirmPw"
                    />

                    <br/>

                    <Button style={styles.saveBtn} onClick={this.onClickSave} color="primary"
                            disabled={this.state.newPw.length === 0 || this.state.confirmPw.length === 0 ||
                            this.state.currPw.length === 0}
                    >
                        Save
                    </Button>
                </DialogContent>
            </Dialog>
        );
    }
}


ChangePasswordForm.propTypes = {
    fullWidth: PropTypes.bool,
    maxWidth: PropTypes.bool,
    onBackdropClick: PropTypes.func,
    onClose: PropTypes.func,
    onEnter: PropTypes.func,
    onEntered: PropTypes.func,
    onEntering: PropTypes.func,
    onEscapeKeyDown: PropTypes.func,
    onExit: PropTypes.func,
    onExited: PropTypes.func,
    onExiting: PropTypes.func,
    open: PropTypes.bool,
    classes: PropTypes.object,

    withBackButton: PropTypes.bool,
    onClickBack: PropTypes.func,
    onClickSave: PropTypes.func
};

ChangePasswordForm.defaultProps = {
    withBackButton: true
};


export default connect(null, {
    changePassword,
    showAlert,
    closeAlert
})(ChangePasswordForm);