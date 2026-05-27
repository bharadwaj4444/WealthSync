package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

// --- 1. Entities ---

@Entity(tableName = "financial_goals")
data class FinancialGoal(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentSaved: Double,
    val targetYear: Int,
    val targetMonth: Int,
    val riskProfile: String // "Conservative", "Moderate", "Aggressive"
) {
    val progress: Float
        get() = if (targetAmount > 0) (currentSaved / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
}

@Entity(tableName = "portfolio_holdings")
data class Holding(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val assetClass: String, // "Equity", "Debt", "Gold", "Cash"
    val assetName: String,
    val value: Double
)

@Entity(tableName = "monthly_snapshots")
data class MonthlySnapshot(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val monthYear: String, // e.g. "2026-01", "2026-02"
    val netWorth: Double,
    val savingsRate: Double // e.g., 25.0 %
)

// --- 2. DAO ---

@Dao
interface FinancialDao {
    // Goals
    @Query("SELECT * FROM financial_goals ORDER BY targetYear ASC, targetMonth ASC")
    fun getAllGoals(): Flow<List<FinancialGoal>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: FinancialGoal)

    @Update
    suspend fun updateGoal(goal: FinancialGoal)

    @Query("DELETE FROM financial_goals WHERE id = :id")
    suspend fun deleteGoal(id: Int)

    // Holdings
    @Query("SELECT * FROM portfolio_holdings ORDER BY value DESC")
    fun getAllHoldings(): Flow<List<Holding>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHolding(holding: Holding)

    @Update
    suspend fun updateHolding(holding: Holding)

    @Query("DELETE FROM portfolio_holdings WHERE id = :id")
    suspend fun deleteHolding(id: Int)

    // Snapshots
    @Query("SELECT * FROM monthly_snapshots ORDER BY monthYear ASC")
    fun getAllSnapshots(): Flow<List<MonthlySnapshot>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnapshot(snapshot: MonthlySnapshot)

    @Query("DELETE FROM monthly_snapshots WHERE id = :id")
    suspend fun deleteSnapshot(id: Int)

    @Query("DELETE FROM financial_goals")
    suspend fun clearAllGoals()

    @Query("DELETE FROM portfolio_holdings")
    suspend fun clearAllHoldings()

    @Query("DELETE FROM monthly_snapshots")
    suspend fun clearAllSnapshots()
}

// --- 3. Database ---

@Database(entities = [FinancialGoal::class, Holding::class, MonthlySnapshot::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dao(): FinancialDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "financial_planner_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

// --- 4. Repository with Data Seeding Support ---

class FinancialRepository(private val dao: FinancialDao) {
    val allGoals: Flow<List<FinancialGoal>> = dao.getAllGoals()
    val allHoldings: Flow<List<Holding>> = dao.getAllHoldings()
    val allSnapshots: Flow<List<MonthlySnapshot>> = dao.getAllSnapshots()

    suspend fun insertGoal(goal: FinancialGoal) = dao.insertGoal(goal)
    suspend fun updateGoal(goal: FinancialGoal) = dao.updateGoal(goal)
    suspend fun deleteGoal(id: Int) = dao.deleteGoal(id)

    suspend fun insertHolding(holding: Holding) = dao.insertHolding(holding)
    suspend fun updateHolding(holding: Holding) = dao.updateHolding(holding)
    suspend fun deleteHolding(id: Int) = dao.deleteHolding(id)

    suspend fun insertSnapshot(snapshot: MonthlySnapshot) = dao.insertSnapshot(snapshot)
    suspend fun deleteSnapshot(id: Int) = dao.deleteSnapshot(id)

    suspend fun clearAllData() {
        dao.clearAllGoals()
        dao.clearAllHoldings()
        dao.clearAllSnapshots()
    }

    /**
     * Seeds realistic starter portfolio and milestones if the database is brand new.
     */
    suspend fun seedMockDataIfEmpty(
        currentGoals: List<FinancialGoal>,
        currentHoldings: List<Holding>,
        currentSnapshots: List<MonthlySnapshot>
    ) {
        // No preloaded mock data to ensure clean, empty starting slate as requested.
    }
}
