import React, { Component } from 'react';
import '../styles/style.scss';

export default class App extends Component {
  render() {
    return (
      <div>
        Hello React
        {this.props.children}
      </div>
    );
  }
}
