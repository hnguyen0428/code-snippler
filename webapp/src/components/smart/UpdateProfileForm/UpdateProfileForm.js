import React, { Component } from 'react';
import PropTypes from 'prop-types';
import {connect} from 'react-redux';

import Dialog from '@material-ui/core/Dialog';
import DialogTitle from '@material-ui/core/DialogTitle';
import DialogContent from '@material-ui/core/DialogContent';
import Button from '@material-ui/core/Button';
import IconButton from '@material-ui/core/IconButton';
import InputLabel from '@material-ui/core/InputLabel';
import Input from '@material-ui/core/Input';
import InputAdornment from '@material-ui/core/InputAdornment';

import Mail from '@material-ui/icons/Mail';
import PermContactCalendar from '@material-ui/icons/PermContactCalendar';
import ArrowBackIos from '@material-ui/icons/ArrowBackIos';

import {updateProfile, fetchMe} from "../../../redux/actions/userActions";
import {showAlert, closeAlert} from "../../../redux/actions/alertActions";

import {styles} from './styles';
import Utility from "../../../util/Utility";


class UpdateProfileForm extends Component {
    INV_EMAIL = 'Email is not valid';
    FIRST_NAME_LIMIT = 256;
    LAST_NAME_LIMIT = 256;
    EMAIL_LIMIT = 256;


    constructor(props) {
        super(props);

        let user = this.props.auth.currentUser;
        if (user && user.profile)
            this.state = {
                firstName: user.profile.firstName ? user.profile.firstName : '',
                lastName: user.profile.lastName ? user.profile.lastName : '',
                email: user.profile.email ? user.profile.email : ''
            };
        else
            this.state = {
                firstName: '',
                lastName: '',
                email: ''
            };
    }


    componentWillMount() {
        this.props.fetchMe();
    }



    sanityCheck = () => {
        let errors = [];

        if (this.state.email.length !== 0) {
            let regexTest = Utility.validateEmail(this.state.email);
            if (!regexTest) {
                errors.push(this.INV_EMAIL);
            }
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

        const params = {
            firstName: this.state.firstName,
            lastName: this.state.lastName,
            email: this.state.email
        };

        this.props.updateProfile(params, (res, err) => {
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

                    <DialogTitle>Update Your Information</DialogTitle>

                    <Input style={styles.input} placeholder="First Name"
                           startAdornment={
                               <InputAdornment style={styles.adornment} position="start">
                                   <PermContactCalendar/>
                               </InputAdornment>
                           }
                           value={this.state.firstName}
                           onChange={this.onInputEdit}
                           name="firstName"
                    />
                    <InputLabel>{this.state.firstName.length}/{this.FIRST_NAME_LIMIT}</InputLabel>

                    <br/>

                    <Input style={styles.input} placeholder="Last Name"
                           startAdornment={
                               <InputAdornment style={styles.adornment} position="start">
                                   <PermContactCalendar/>
                               </InputAdornment>
                           }
                           value={this.state.lastName}
                           onChange={this.onInputEdit}
                           name="lastName"
                    />
                    <InputLabel>{this.state.lastName.length}/{this.LAST_NAME_LIMIT}</InputLabel>

                    <br/>

                    <Input style={styles.input} placeholder="Email"
                           startAdornment={
                               <InputAdornment style={styles.adornment} position="start">
                                   <Mail/>
                               </InputAdornment>
                           }
                           value={this.state.email}
                           onChange={this.onInputEdit}
                           name="email"
                    />
                    <InputLabel>{this.state.email.length}/{this.EMAIL_LIMIT}</InputLabel>

                    <br/>

                    <Button style={styles.saveBtn} onClick={this.onClickSave} color="primary"
                            disabled={this.state.firstName.length === 0 && this.state.lastName.length === 0 &&
                            this.state.email.length === 0}
                    >
                        Save
                    </Button>
                </DialogContent>
            </Dialog>
        );
    }
}


function mapStateToProps(state) {
    return {
        auth: state.auth
    }
}


UpdateProfileForm.propTypes = {
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

UpdateProfileForm.defaultProps = {
    withBackButton: true
};


export default connect(mapStateToProps, {
    fetchMe,
    updateProfile,
    showAlert,
    closeAlert
})(UpdateProfileForm);