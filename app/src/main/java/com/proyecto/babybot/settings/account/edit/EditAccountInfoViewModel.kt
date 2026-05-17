package com.proyecto.babybot.settings.account.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto.babybot.data.firebase.AuthDataSource
import com.proyecto.babybot.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditAccountInfoViewModel @Inject constructor(
    private val authDataSource: AuthDataSource,
    private val homeRepository: HomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(EditAccountInfoState())
    val state = _state.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        val userId = authDataSource.getCurrentUserId()

        if (userId.isNullOrBlank()) {
            _state.update {
                it.copy(
                    isLoading = false,
                    errorMessage = "No se encontró una sesión activa."
                )
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                    savedSuccessfully = false
                )
            }

            val baby = homeRepository.getBabyByUserId(userId)
            val profileResult = authDataSource.getCurrentUserProfile()
            val licenseInfo = authDataSource.getLicenseInfo()

            val profileData = profileResult.getOrNull().orEmpty()

            _state.update {
                it.copy(
                    isLoading = false,
                    userName = profileData["nombre"] as? String
                        ?: authDataSource.getCurrentUserName().orEmpty(),
                    userEmail = profileData["correo"] as? String
                        ?: authDataSource.getCurrentUserEmail().orEmpty(),
                    licenseType = licenseInfo.type,
                    licenseStatus = licenseInfo.status,
                    licenseExpirationDateMillis = licenseInfo.expirationDateMillis,
                    baby = baby
                )
            }
        }
    }
    fun updateUserName(name: String) {
        val cleanName = name.trim()

        if (cleanName.isBlank()) {
            _state.update {
                it.copy(errorMessage = "El nombre no puede estar vacío.")
            }
            return
        }

        if (cleanName.length < 2) {
            _state.update {
                it.copy(errorMessage = "El nombre debe tener al menos 2 caracteres.")
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null,
                    savedSuccessfully = false
                )
            }

            val result = authDataSource.updateUserName(cleanName)

            result.onSuccess {
                _state.update {
                    it.copy(
                        isSaving = false,
                        userName = cleanName,
                        savedSuccessfully = true
                    )
                }
            }.onFailure { error ->
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "No se pudo actualizar el perfil."
                    )
                }
            }
        }
    }

    fun updateBaby(
        name: String,
        gender: String,
        birthDate: Long,
        weight: Double,
        height: Double,
        bloodType: String,
        pediatrician: String,
        notes: String,
        allergies: List<String>
    ) {
        val currentBaby = _state.value.baby

        if (currentBaby == null) {
            _state.update {
                it.copy(errorMessage = "No se encontró información del bebé.")
            }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isSaving = true,
                    errorMessage = null,
                    savedSuccessfully = false
                )
            }

            val success = homeRepository.updateBaby(
                baby = currentBaby,
                name = name,
                gender = gender,
                birthDate = birthDate,
                weight = weight,
                height = height,
                bloodType = bloodType,
                pediatrician = pediatrician,
                notes = notes,
                allergies = allergies
            )

            if (success) {
                _state.update {
                    it.copy(
                        isSaving = false,
                        savedSuccessfully = true
                    )
                }
            } else {
                _state.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = "No se pudo actualizar la información del bebé."
                    )
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }
}