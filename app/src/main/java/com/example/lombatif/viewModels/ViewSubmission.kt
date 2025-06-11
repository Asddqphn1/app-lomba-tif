package com.example.lombatif.viewModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.response.DataLombaUser
import com.example.lombatif.response.SubmissionData
import com.example.lombatif.response.SubmissionRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException


// State untuk daftar lomba yang diikuti
sealed interface LombaListState {
    object Loading : LombaListState
    data class Success(val lombaList: List<DataLombaUser>) : LombaListState
    data class Error(val message: String) : LombaListState
}

// State untuk hasil pengecekan submission
sealed interface SubmissionCheckState {
    object Idle : SubmissionCheckState
    object Loading : SubmissionCheckState
    data class Success(val submission: SubmissionData?) : SubmissionCheckState // Nullable jika belum ada submission
    data class Error(val message: String) : SubmissionCheckState
}

// State untuk proses submit/delete
sealed interface ActionState {
    object Idle : ActionState
    object Loading : ActionState
    data class Success(val message: String) : ActionState
    data class Error(val message: String) : ActionState
}

class ViewSubmission : ViewModel() {

    // State untuk daftar lomba yang diikuti
    private val _lombaListState = MutableStateFlow<LombaListState>(LombaListState.Loading)
    val lombaListState = _lombaListState.asStateFlow()

    // State untuk pengecekan submission
    private val _submissionCheckState = MutableStateFlow<SubmissionCheckState>(SubmissionCheckState.Idle)
    val submissionCheckState = _submissionCheckState.asStateFlow()

    // State untuk aksi submit URL
    private val _submitState = MutableStateFlow<ActionState>(ActionState.Idle)
    val submitState = _submitState.asStateFlow()

    // State untuk aksi hapus submission
    private val _deleteState = MutableStateFlow<ActionState>(ActionState.Idle)
    val deleteState = _deleteState.asStateFlow()


    // 1. Mengambil daftar lomba yang diikuti user
    fun fetchJoinedLomba(userId: String) {
        viewModelScope.launch {
            _lombaListState.value = LombaListState.Loading
            try {
                val response = Retrofins.api.getLombaDiikuti(userId)
                _lombaListState.value = LombaListState.Success(response.data)
            } catch (e: Exception) {
                _lombaListState.value = LombaListState.Error(e.message ?: "Gagal memuat data lomba")
            }
        }
    }

    // 2. Mengecek status submission saat layar form dibuka
    fun checkSubmissionStatus(idPesertaLomba: String) {
        viewModelScope.launch {
            _submissionCheckState.value = SubmissionCheckState.Loading
            try {
                // Retrofit akan melempar HttpException jika status code bukan 2xx
                val response = Retrofins.api.checkSubmission(idPesertaLomba)
                // Ini hanya akan dieksekusi jika status code 200 OK (submission ditemukan)
                _submissionCheckState.value = SubmissionCheckState.Success(response.data)

            } catch (e: HttpException) {
                // Jika error adalah 404, anggap sebagai sukses dengan data null
                if (e.code() == 404) {
                    _submissionCheckState.value = SubmissionCheckState.Success(null)
                } else {
                    // Untuk error HTTP lain (500, 401, dll), tampilkan sebagai error
                    _submissionCheckState.value = SubmissionCheckState.Error("Error: ${e.message()}")
                }
            } catch (e: Exception) {
                // Untuk error non-HTTP (misal: tidak ada internet)
                _submissionCheckState.value = SubmissionCheckState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    // 3. Melakukan submit URL
    fun submitUrl(idPesertaLomba: String, url: String) {
        viewModelScope.launch {
            _submitState.value = ActionState.Loading
            try {
                val response = Retrofins.api.postSubmission(idPesertaLomba, SubmissionRequest(url))
                _submitState.value = ActionState.Success(response.message)
                // Setelah sukses, cek ulang statusnya agar UI update
                checkSubmissionStatus(idPesertaLomba)
            } catch (e: Exception) {
                _submitState.value = ActionState.Error(e.message ?: "Gagal melakukan submission")
            }
        }
    }

    // 4. Menghapus submission
    fun deleteSubmission(submissionId: String, idPesertaLomba: String) {
        viewModelScope.launch {
            _deleteState.value = ActionState.Loading
            try {
                val response = Retrofins.api.deleteSubmission(submissionId)
                _deleteState.value = ActionState.Success(response.message)
                // Setelah sukses hapus, cek ulang statusnya agar UI kembali ke form
                checkSubmissionStatus(idPesertaLomba)
            } catch (e: Exception) {
                _deleteState.value = ActionState.Error(e.message ?: "Gagal menghapus submission")
            }
        }
    }

    // Fungsi untuk mereset state pop-up/dialog
    fun resetActionStates() {
        _submitState.value = ActionState.Idle
        _deleteState.value = ActionState.Idle
    }
}