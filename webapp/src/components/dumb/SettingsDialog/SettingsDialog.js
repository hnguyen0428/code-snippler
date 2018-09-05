import React, { Component } from 'react';
import PropTypes from 'prop-types';

import Dialog from '@material-ui/core/Dialog';
import DialogContent from '@material-ui/core/DialogContent';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';

import PermIdentity from '@material-ui/icons/PermIdentity';
import Lock from '@material-ui/icons/Lock';
import Code from '@material-ui/icons/Code';

import {styles} from './styles';


class SettingsDialog extends Component {
    render() {
        let {...props} = this.props;

        return (
            <Dialog {...props}>
                <div style={styles.dialogCtn}>
                    <List>
                        <ListItem button onClick={this.props.onClickProfile}>
                            <ListItemIcon>
                                <PermIdentity/>
                            </ListItemIcon>
                            <ListItemText primary="Profile Information"/>
                        </ListItem>
                        <ListItem button onClick={this.props.onClickPassword}>
                            <ListItemIcon>
                                <Lock/>
                            </ListItemIcon>
                            <ListItemText primary="Change Password"/>
                        </ListItem>
                        <ListItem button onClick={this.props.onClickEditor}>
                            <ListItemIcon>
                                <Code/>
                            </ListItemIcon>
                            <ListItemText primary="Code Editor Settings"/>
                        </ListItem>
                    </List>
                </div>
            </Dialog>
        );
    }
}


SettingsDialog.propTypes = {
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
    onClickProfile: PropTypes.func,
    onClickPassword: PropTypes.func,
    onClickEditor: PropTypes.func
};



export default SettingsDialog;