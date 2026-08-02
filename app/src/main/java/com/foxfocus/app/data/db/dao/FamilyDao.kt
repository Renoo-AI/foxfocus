package com.foxfocus.app.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.foxfocus.app.data.db.entity.FamilyMemberEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FamilyDao {
  @Query("SELECT * FROM family_members")
  fun getFamilyMembers(): Flow<List<FamilyMemberEntity>>

  @Query("SELECT * FROM family_members")
  suspend fun getFamilyMembersList(): List<FamilyMemberEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertFamilyMembers(members: List<FamilyMemberEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertMember(member: FamilyMemberEntity)

  @Query("DELETE FROM family_members WHERE memberId = :memberId")
  suspend fun deleteMember(memberId: String)

  @Query("SELECT COUNT(*) FROM family_members")
  suspend fun getMemberCount(): Int
}
