package com.mavi.data.repository

import com.mavi.data.local.AppDatabase
import com.mavi.data.local.UserEntity
import com.mavi.network.ApiClient
import com.mavi.network.LoginRequest
import com.mavi.network.RegisterRequest

class AuthRepository(private val db: AppDatabase) {
    private val authService = ApiClient.authService

    suspend fun register(username: String, password: String): Result<String> = try {
        val response = authService.register(RegisterRequest(username, password))
        if (response.isSuccessful) {
            val token = response.body()?.access_token
            token?.let { 
                ApiClient.setAuthToken(it)
                Result.success(it)
            } ?: Result.failure(Exception("No token received"))
        } else {
            Result.failure(Exception(response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun login(username: String, password: String): Result<String> = try {
        val response = authService.login(LoginRequest(username, password))
        if (response.isSuccessful) {
            val token = response.body()?.access_token
            token?.let { 
                ApiClient.setAuthToken(it)
                response.body()?.user?.let { user ->
                    db.userDao().insertUser(
                        UserEntity(user.id, user.username, user.created_at)
                    )
                }
                Result.success(it)
            } ?: Result.failure(Exception("No token received"))
        } else {
            Result.failure(Exception(response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    suspend fun logout() {
        ApiClient.clearAuthToken()
        db.userDao().deleteAllUsers()
        db.messageDao().deleteAll()
        db.fileTransferDao().deleteAll()
        db.callDao().deleteAll()
    }

    suspend fun getAllUsers() = try {
        val response = authService.getAllUsers()
        if (response.isSuccessful) {
            response.body()?.let { users ->
                db.userDao().insertUsers(users.map { 
                    UserEntity(it.id, it.username, it.created_at)
                })
                Result.success(users)
            } ?: Result.failure(Exception("No users received"))
        } else {
            Result.failure(Exception(response.message()))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
