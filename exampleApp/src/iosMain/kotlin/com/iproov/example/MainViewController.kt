// Copyright (c) 2024 iProov Ltd. All rights reserved.

package com.iproov.example

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

public fun MainViewController(): UIViewController = ComposeUIViewController { App() }