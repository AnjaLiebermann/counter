package org.example.counter

import com.vaadin.flow.component.page.AppShellConfigurator
import com.vaadin.flow.component.page.Push
import com.vaadin.flow.shared.communication.PushMode
import com.vaadin.flow.shared.ui.Transport

@Push(value = PushMode.AUTOMATIC, transport = Transport.WEBSOCKET_XHR)
class AppShell : AppShellConfigurator

