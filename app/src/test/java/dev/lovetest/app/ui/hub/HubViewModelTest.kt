package dev.lovetest.app.ui.hub

import org.junit.Assert.assertEquals
import org.junit.Test

class HubViewModelTest {

    @Test
    fun initialState_withoutDebugPreview_isMain() {
        val viewModel = HubViewModel()
        assertEquals(HubDisplayState.Main, viewModel.state.value)
    }

    @Test
    fun retryFromError_setsMain() {
        val viewModel = HubViewModel()
        viewModel.retryFromError()
        assertEquals(HubDisplayState.Main, viewModel.state.value)
    }
}
