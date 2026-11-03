package com.findit.app

import com.findit.app.data.repository.ItemRepository
import com.findit.app.domain.model.Item
import com.findit.app.domain.model.ItemCategory
import com.findit.app.domain.model.ItemStatus
import com.findit.app.domain.model.ItemType
import com.findit.app.presentation.report.ReportState
import com.findit.app.presentation.report.ReportViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ReportViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: ItemRepository
    private lateinit var viewModel: ReportViewModel

    class FakeItemRepository : ItemRepository {
        var reportCalled = false
        var lastReportedItem: Item? = null
        var reportError: Exception? = null

        override fun getAllItems(): Flow<List<Item>> = flowOf(emptyList())

        override fun getItemById(id: String): Flow<Item?> = flowOf(null)

        override suspend fun reportItem(item: Item) {
            reportError?.let { throw it }
            reportCalled = true
            lastReportedItem = item
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeItemRepository()
        viewModel = ReportViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun reportItem_successState() = runTest {
        // When reporting an item
        viewModel.reportItem(
            name = "Test Wallet",
            category = "Wallets",
            location = "Gym",
            date = "Today",
            time = null,
            description = "Black leather wallet",
            type = ItemType.LOST,
            reportedBy = "Alex Rivera",
            reportedById = "alex@school.edu"
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // Then repository should receive the report and state should be Success
        assertEquals(true, (fakeRepository as FakeItemRepository).reportCalled)
        assertEquals("Test Wallet", (fakeRepository as FakeItemRepository).lastReportedItem?.name)
        assertEquals(ReportState.Success, viewModel.reportState.value)
    }

    @Test
    fun reportItem_repositoryFailureSetsErrorState() = runTest {
        (fakeRepository as FakeItemRepository).reportError = IllegalStateException("Firestore unavailable")

        viewModel.reportItem(
            name = "Test Wallet",
            category = "Wallets",
            location = "Gym",
            date = "Today",
            time = null,
            description = "Black leather wallet",
            type = ItemType.LOST,
            reportedBy = "Alex Rivera",
            reportedById = "alex@school.edu"
        )

        testDispatcher.scheduler.advanceUntilIdle()

        assertEquals(ReportState.Error("Firestore unavailable"), viewModel.reportState.value)
    }
}
