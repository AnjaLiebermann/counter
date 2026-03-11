package org.example.counter

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.notification.Notification
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.textfield.IntegerField
import com.vaadin.flow.router.Route
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate

@Route("")
class CountdownView : VerticalLayout() {

    private val minutesInput = IntegerField()
    private val startButton = Button("Start")
    private val pauseButton = Button("Pause")
    private val resetButton = Button("Reset")
    private val minutesDisplay = Span("00")
    private val secondsDisplay = Span("00")
    private val separator = Span(":")

    private var timerTask: TimerTask? = null
    private var timer: Timer? = null
    private var remainingSeconds = 0

    init {
        // Main container styling
        setSizeFull()
        alignItems = FlexComponent.Alignment.CENTER
        justifyContentMode = FlexComponent.JustifyContentMode.CENTER
        style.set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")

        // Card container
        val card = VerticalLayout().apply {
            width = "500px"
            style.apply {
                set("background", "white")
                set("border-radius", "20px")
                set("box-shadow", "0 20px 60px rgba(0,0,0,0.3)")
                set("padding", "40px")
            }
            alignItems = FlexComponent.Alignment.CENTER
        }

        // Title
        val title = H1("⏱️ Countdown Timer").apply {
            style.apply {
                set("margin", "0 0 30px 0")
                set("color", "#333")
                set("font-weight", "700")
            }
        }

        // Input field styling
        minutesInput.apply {
            value = 1
            min = 1
            max = 999
            width = "200px"
            label = "Minutes"
            style.apply {
                set("margin-bottom", "20px")
            }
        }

        // Countdown display container
        val displayContainer = Div().apply {
            style.apply {
                set("display", "flex")
                set("align-items", "center")
                set("justify-content", "center")
                set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                set("border-radius", "15px")
                set("padding", "30px 50px")
                set("margin", "30px 0")
                set("box-shadow", "0 10px 30px rgba(102, 126, 234, 0.4)")
            }
        }

        // Style for time digits
        listOf(minutesDisplay, secondsDisplay).forEach { span ->
            span.style.apply {
                set("font-size", "72px")
                set("font-weight", "700")
                set("color", "white")
                set("font-family", "monospace")
                set("min-width", "100px")
                set("text-align", "center")
            }
        }

        // Separator styling
        separator.style.apply {
            set("font-size", "72px")
            set("font-weight", "700")
            set("color", "white")
            set("margin", "0 10px")
        }

        displayContainer.add(minutesDisplay, separator, secondsDisplay)

        // Button container
        val buttonLayout = HorizontalLayout().apply {
            setWidthFull()
            justifyContentMode = FlexComponent.JustifyContentMode.CENTER
            style.set("gap", "15px")
        }

        // Start button styling
        startButton.apply {
            addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE)
            style.apply {
                set("background", "linear-gradient(135deg, #667eea 0%, #764ba2 100%)")
                set("border", "none")
                set("padding", "12px 30px")
                set("font-weight", "600")
                set("cursor", "pointer")
            }
            addClickListener { startCountdown() }
        }

        // Pause button styling
        pauseButton.apply {
            isEnabled = false
            addThemeVariants(ButtonVariant.LUMO_CONTRAST, ButtonVariant.LUMO_LARGE)
            style.apply {
                set("padding", "12px 30px")
                set("font-weight", "600")
                set("cursor", "pointer")
            }
            addClickListener { pauseCountdown() }
        }

        // Reset button styling
        resetButton.apply {
            isEnabled = false
            addThemeVariants(ButtonVariant.LUMO_ERROR, ButtonVariant.LUMO_LARGE)
            style.apply {
                set("padding", "12px 30px")
                set("font-weight", "600")
                set("cursor", "pointer")
            }
            addClickListener { resetCountdown() }
        }

        buttonLayout.add(startButton, pauseButton, resetButton)

        // Add all components to card
        card.add(title, minutesInput, displayContainer, buttonLayout)

        // Add card to main layout
        add(card)
    }

    private fun startCountdown() {
        // Only set initial time if starting fresh (remainingSeconds is 0)
        if (remainingSeconds == 0) {
            val minutes = minutesInput.value ?: 1
            remainingSeconds = minutes * 60
        }

        minutesInput.isEnabled = false
        startButton.isEnabled = false
        pauseButton.isEnabled = true
        resetButton.isEnabled = true

        // Update display immediately to show starting time
        updateDisplay()

        timerTask?.cancel()
        timer?.cancel()
        timer = Timer()
        timerTask = timer?.scheduleAtFixedRate(1000, 1000) {
            ui.ifPresent { ui ->
                ui.access {
                    remainingSeconds--
                    if (remainingSeconds >= 0) {
                        updateDisplay()
                    }
                    if (remainingSeconds < 0) {
                        finishCountdown()
                    }
                }
            }
        }
    }

    private fun playSound() {
        // Play a beep sound using the Web Audio API
        ui.ifPresent { ui ->
            ui.page.executeJs("""
                const audioContext = new (window.AudioContext || window.webkitAudioContext)();
                const oscillator = audioContext.createOscillator();
                const gainNode = audioContext.createGain();

                oscillator.connect(gainNode);
                gainNode.connect(audioContext.destination);

                oscillator.frequency.value = 800;
                oscillator.type = 'sine';

                gainNode.gain.setValueAtTime(0.3, audioContext.currentTime);
                gainNode.gain.exponentialRampToValueAtTime(0.01, audioContext.currentTime + 0.5);

                oscillator.start(audioContext.currentTime);
                oscillator.stop(audioContext.currentTime + 0.5);
            """)
        }
    }

    private fun pauseCountdown() {
        timerTask?.cancel()
        timer?.cancel()
        timerTask = null
        timer = null

        startButton.isEnabled = true
        pauseButton.isEnabled = false
        // Keep resetButton enabled so user can reset if they want
    }

    private fun resetCountdown() {
        timerTask?.cancel()
        timer?.cancel()
        timerTask = null
        timer = null

        minutesInput.isEnabled = true
        startButton.isEnabled = true
        pauseButton.isEnabled = false
        resetButton.isEnabled = false

        remainingSeconds = 0
        updateDisplay()
    }

    private fun finishCountdown() {
        timerTask?.cancel()
        timer?.cancel()
        timerTask = null
        timer = null

        minutesInput.isEnabled = true
        startButton.isEnabled = true
        pauseButton.isEnabled = false
        resetButton.isEnabled = false

        remainingSeconds = 0
        updateDisplay()

        playSound()
        Notification.show("Countdown finished!", 3000, Notification.Position.MIDDLE)
    }

    private fun updateDisplay() {
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60
        minutesDisplay.text = String.format("%02d", minutes)
        secondsDisplay.text = String.format("%02d", seconds)
    }
}

