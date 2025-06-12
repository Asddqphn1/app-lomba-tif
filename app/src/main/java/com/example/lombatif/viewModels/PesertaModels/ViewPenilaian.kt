package com.example.lombatif.viewModels.PesertaModels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lombatif.api.Retrofins
import com.example.lombatif.models.get.modelsPeserta.KlasemenEntry
import com.example.lombatif.models.get.modelsPeserta.PesertaPenilaian
import com.example.lombatif.models.get.modelsPeserta.SertifikatData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// State untuk UI halaman penilaian
sealed interface PenilaianState {
    object Loading : PenilaianState
    // Sukses akan mengembalikan list data peserta (meski biasanya hanya 1)
    data class Success(val pesertaList: List<PesertaPenilaian>) : PenilaianState
    data class Error(val message: String) : PenilaianState
}

// State baru untuk UI Sertifikat
sealed interface SertifikatState {
    object Idle : SertifikatState
    object Loading : SertifikatState
    data class Success(val sertifikat: SertifikatData) : SertifikatState
    data class Error(val message: String) : SertifikatState
}

sealed interface KlasemenState {
    object Idle : KlasemenState
    object Loading : KlasemenState
    data class Success(val klasemenList: List<KlasemenEntry>) : KlasemenState
    data class Error(val message: String) : KlasemenState
}

class ViewPenilaian : ViewModel() {
    private val _penilaianState = MutableStateFlow<PenilaianState>(PenilaianState.Loading)
    val penilaianState = _penilaianState.asStateFlow()

    private val _sertifikatState = MutableStateFlow<SertifikatState>(SertifikatState.Idle)
    val sertifikatState = _sertifikatState.asStateFlow()


    private val _klasemenState = MutableStateFlow<KlasemenState>(KlasemenState.Idle)
    val klasemenState = _klasemenState.asStateFlow()

    fun fetchPenilaian(userId: String) {
        viewModelScope.launch {
            _penilaianState.value = PenilaianState.Loading
            try {
                val response = Retrofins.apiServicePeserta.getPenilaian(userId)
                if (response.status == "success") {
                    _penilaianState.value = PenilaianState.Success(response.data)
                } else {
                    _penilaianState.value = PenilaianState.Error(response.message)
                }
            } catch (e: Exception) {
                _penilaianState.value = PenilaianState.Error(e.message ?: "Gagal memuat data penilaian")
            }
        }
    }

    // FUNGSI BARU UNTUK MENGAMBIL DAN MEMPROSES DATA KLASMEN
    fun fetchKlasemen(lombaId: String) {
        viewModelScope.launch {
            _klasemenState.value = KlasemenState.Loading
            try {
                val response = Retrofins.apiServicePeserta.getKlasemen(lombaId)
                if (response.success) {
                    // 1. Kelompokkan nilai berdasarkan nama peserta
                    val groupedByPeserta = response.data.groupBy { it.peserta.nama }

                    // 2. Hitung rata-rata dan jumlah penilaian untuk setiap peserta
                    val processedList = groupedByPeserta.map { (nama, nilaiList) ->
                        val totalNilai = nilaiList.sumOf { it.nilai?.toDoubleOrNull() ?: 0.0 }
                        val jumlahPenilaian = nilaiList.size
                        val rataRata = if (jumlahPenilaian > 0) totalNilai / jumlahPenilaian else 0.0

                        // Buat data sementara untuk diurutkan
                        Triple(nama, rataRata, jumlahPenilaian)
                    }

                    // 3. Urutkan berdasarkan nilai rata-rata tertinggi
                    val sortedList = processedList.sortedByDescending { it.second }

                    // 4. Buat data final dengan peringkat
                    val finalList = sortedList.mapIndexed { index, (nama, rataRata, jumlah) ->
                        KlasemenEntry(
                            rank = index + 1,
                            namaPeserta = nama,
                            rataRataNilai = rataRata,
                            jumlahPenilaian = jumlah
                        )
                    }

                    _klasemenState.value = KlasemenState.Success(finalList)
                } else {
                    _klasemenState.value = KlasemenState.Error("Gagal memuat data klasemen.")
                }
            } catch (e: Exception) {
                _klasemenState.value = KlasemenState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }

    // Fungsi untuk mereset state klasemen setelah dialog ditutup
    fun clearKlasemenState() {
        _klasemenState.value = KlasemenState.Idle
    }

    fun fetchSertifikat(lombaId: String) {
        viewModelScope.launch {
            _sertifikatState.value = SertifikatState.Loading
            try {
                val response = Retrofins.apiServicePeserta.getSertifikat(lombaId)
                if (response.status == "success" && response.data != null) {
                    _sertifikatState.value = SertifikatState.Success(response.data)
                } else {
                    _sertifikatState.value = SertifikatState.Error(response.message)
                }
            } catch (e: Exception) {
                _sertifikatState.value = SertifikatState.Error(e.message ?: "Gagal memuat sertifikat")
            }
        }
    }

    // Fungsi untuk mereset state sertifikat setelah dialog ditutup
    fun clearSertifikatState() {
        _sertifikatState.value = SertifikatState.Idle
    }
}
