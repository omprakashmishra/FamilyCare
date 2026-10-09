# FamilyCare: MVVM + Clean Architecture Refactoring Guide

## Overview
This guide refactors FamilyCare to follow **MVVM (Model-View-ViewModel)** with **Clean Architecture** principles using Kotlin coroutines, Flow, and Hilt.

---

## Architecture Layers

### 1. **Presentation Layer** (`ui/`)
- **Activities/Fragments**: Pure UI layer — receives UI state from ViewModels
- **ViewModels**: Business logic orchestration, state management, event handling
- **Adapters**: Data presentation adapters (RecyclerView, etc.)

**Key Principles:**
- Activities/Fragments are dumb — they only display state and forward user input
- No direct repository calls from UI
- State flows unidirectionally: ViewModel → UI
- Events flow back via callbacks or channel-based communication

### 2. **Domain Layer** (`domain/`)
- **Use Cases**: Pure Kotlin — encapsulate specific business logic
- **Entities**: Core business models (independent of data source)
- **Repositories (Interfaces)**: Abstract data access contracts

**Key Principles:**
- No Android dependencies
- Reusable across multiple presentation patterns
- Single responsibility per use case
- Testable in isolation

### 3. **Data Layer** (`data/`)
- **Repositories (Implementations)**: Coordinate local + remote data sources
- **Remote Data Sources**: API calls via Retrofit
- **Local Data Sources**: Room, SharedPreferences
- **DTOs (Data Transfer Objects)**: Network/database model serialization
- **Entities**: Room persistence models

**Key Principles:**
- Hide complexity of data fetching
- Implement repository interfaces from domain
- Handle caching, sync, retry logic
- Transform data between source formats and domain entities

---

## Improved Project Structure

```
app/src/main/java/com/omsworld/familycare/
  ├── base/
  │   ├── BaseActivity.kt              # Activity with view binding
  │   ├── BaseViewModel.kt             # VM with state + events
  │   ├── BaseFragment.kt              # Fragment foundation
  │   ├── UiState.kt                   # Common UI state sealed class
  │   └── UiEvent.kt                   # Common UI events
  │
  ├── core/
  │   ├── Constants.kt
  │   ├── AppUtil.kt
  │   ├── Extensions.kt
  │   └── Result.kt                    # NEW: Sealed class for Success/Error/Loading
  │
  ├── domain/
  │   ├── entity/                      # Core business models
  │   │   ├── User.kt
  │   │   ├── FamilyMember.kt
  │   │   └── Location.kt
  │   │
  │   ├── usecase/                     # Business logic (ONE use case = ONE job)
  │   │   ├── auth/
  │   │   │   ├── LoginUseCase.kt
  │   │   │   ├── SignUpUseCase.kt
  │   │   │   └── LogoutUseCase.kt
  │   │   ├── family/
  │   │   │   ├── GetFamilyMembersUseCase.kt
  │   │   │   ├── InviteFamilyUseCase.kt
  │   │   │   └── UpdateFamilyUseCase.kt
  │   │   └── location/
  │   │       ├── StartTrackingUseCase.kt
  │   │       └── GetLocationHistoryUseCase.kt
  │   │
  │   └── repository/                  # Repository interfaces (contracts)
  │       ├── AuthRepository.kt
  │       ├── FamilyRepository.kt
  │       └── LocationRepository.kt
  │
  ├── data/
  │   ├── local/
  │   │   ├── dao/                     # Room DAOs
  │   │   │   ├── UserDao.kt
  │   │   │   ├── FamilyMemberDao.kt
  │   │   │   └── LocationDao.kt
  │   │   ├── entity/                  # Room entities
  │   │   │   ├── UserEntity.kt
  │   │   │   ├── FamilyMemberEntity.kt
  │   │   │   └── LocationEntity.kt
  │   │   ├── FamilyCareDatabase.kt
  │   │   └── MySharedPreference.kt
  │   │
  │   ├── remote/
  │   │   ├── api/
  │   │   │   ├── FamilyCareApi.kt     # Retrofit service
  │   │   │   └── AuthApi.kt
  │   │   ├── dto/                     # Network models (Moshi)
  │   │   │   ├── LoginRequest.kt
  │   │   │   ├── LoginResponse.kt
  │   │   │   └── UserDto.kt
  │   │   └── NetworkMonitor.kt
  │   │
  │   └── repository/                  # Repository implementations
  │       ├── AuthRepositoryImpl.kt
  │       ├── FamilyRepositoryImpl.kt
  │       └── LocationRepositoryImpl.kt
  │
  ├── di/
  │   ├── AppModule.kt                 # App-level singletons
  │   ├── NetworkModule.kt             # Retrofit, OkHttp
  │   ├── DatabaseModule.kt            # Room DB
  │   ├── RepositoryModule.kt          # Bind repository impls
  │   └── UseCaseModule.kt             # Provide use cases
  │
  ├── ui/
  │   ├── auth/
  │   │   ├── SignInUpActivity.kt
  │   │   ├── SignInUpViewModel.kt     # State + Events
  │   │   ├── SignUpActivity.kt
  │   │   ├── SignUpViewModel.kt
  │   │   └── auth_state.kt            # Sealed class for auth states
  │   │
  │   ├── home/
  │   │   ├── HomeFragment.kt
  │   │   ├── HomeViewModel.kt
  │   │   └── home_state.kt
  │   │
  │   ├── family/
  │   │   ├── FamilyMembersFragment.kt
  │   │   ├── FamilyMembersViewModel.kt
  │   │   └── family_state.kt
  │   │
  │   └── main/
  │       ├── MainActivity.kt
  │       └── MainViewModel.kt
  │
  ├── firebase/
  │   └── FirebaseMessagingService.kt
  │
  ├── service/
  │   └── TrackingService.kt
  │
  └── FamilyCareApp.kt
```

---

## Key Implementation Patterns

### 1. **Result Wrapper (for error handling)**

**File:** `core/Result.kt`

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val exception: Throwable) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

### 2. **UI State Pattern**

**File:** `ui/auth/auth_state.kt`

```kotlin
sealed class SignInUiState {
    object Idle : SignInUiState()
    object Loading : SignInUiState()
    data class Success(val userId: String) : SignInUiState()
    data class Error(val msg: String) : SignInUiState()
    data class NeedsOtp(val mobile: String) : SignInUiState()
}
```

### 3. **Domain Use Case**

**File:** `domain/usecase/auth/LoginUseCase.kt`

```kotlin
import javax.inject.Inject
import com.omsworld.familycare.domain.repository.AuthRepository
import com.omsworld.familycare.domain.entity.User
import com.omsworld.familycare.core.Result
import kotlinx.coroutines.flow.Flow

class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(mobile: String, pin: String): Flow<Result<User>> =
        authRepository.login(mobile, pin)
}
```

### 4. **Repository Implementation with Clean Data Flow**

**File:** `data/repository/AuthRepositoryImpl.kt`

```kotlin
import javax.inject.Inject
import com.omsworld.familycare.domain.repository.AuthRepository
import com.omsworld.familycare.domain.entity.User
import com.omsworld.familycare.data.remote.api.AuthApi
import com.omsworld.familycare.data.local.dao.UserDao
import com.omsworld.familycare.core.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val userDao: UserDao,
    private val prefs: MySharedPreference
) : AuthRepository {

    override fun login(mobile: String, pin: String): Flow<Result<User>> = flow {
        emit(Result.Loading)
        try {
            val response = authApi.login(mobile, pin)
            if (response.isSuccessful) {
                val userDto = response.body()?.data
                userDto?.let { dto ->
                    // Map DTO → Domain Entity
                    val user = User(
                        id = dto.userId,
                        mobile = dto.mobile,
                        name = dto.name
                    )
                    // Cache in local DB
                    userDao.insertUser(user.toEntity())
                    // Save token
                    prefs.setString(this@AuthRepositoryImpl, "auth_token", response.body()?.token ?: "")
                    emit(Result.Success(user))
                }
            } else {
                emit(Result.Error(Exception(response.message())))
            }
        } catch (e: Exception) {
            emit(Result.Error(e))
        }
    }
}
```

### 5. **ViewModel with Unidirectional Data Flow**

**File:** `ui/auth/SignInUpViewModel.kt`

```kotlin
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import com.omsworld.familycare.base.BaseViewModel
import com.omsworld.familycare.domain.usecase.auth.LoginUseCase
import com.omsworld.familycare.core.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class SignInUpViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase
) : BaseViewModel<SignInUiState>() {

    override val initialState = SignInUiState.Idle

    fun login(mobile: String, pin: String) {
        viewModelScope.launch {
            loginUseCase(mobile, pin).collect { result ->
                when (result) {
                    is Result.Loading -> setState(SignInUiState.Loading)
                    is Result.Success -> {
                        setState(SignInUiState.Success(result.data.id))
                        // User is logged in, navigate
                        sendEvent(UiEvent.NavigateTo(MainActivity::class.java))
                    }
                    is Result.Error -> {
                        setState(SignInUiState.Error(result.exception.message ?: "Login failed"))
                    }
                }
            }
        }
    }
}
```

### 6. **Activity Observing State**

**File:** `ui/auth/SignInUpActivity.kt`

```kotlin
@AndroidEntryPoint
class SignInUpActivity : BaseActivity<ActivitySigninBinding>() {

    private val vm: SignInUpViewModel by viewModels()

    override fun inflateBinding(inflater: LayoutInflater) =
        ActivitySigninBinding.inflate(inflater)

    override fun onBindingReady() {
        binding.btSignIn.setOnClickListener { onSignInClick() }
        observeState()
    }

    private fun onSignInClick() {
        val mobile = binding.etMobile.text.toString().trim()
        val pin = binding.etPassword.text.toString().trim()
        val code = binding.ccp.selectedCountryCode.toString()
        
        if (mobile.isBlank() || pin.isBlank()) {
            snack(binding.root, getString(R.string.enter_valid_mobile_and_pin))
            return
        }
        
        vm.login(code + mobile, pin)
    }

    private fun observeState() {
        collectState(vm.state) { state ->
            when (state) {
                is SignInUiState.Idle -> Unit
                is SignInUiState.Loading -> {
                    binding.mprogressBar.visibility = View.VISIBLE
                }
                is SignInUiState.Success -> {
                    binding.mprogressBar.visibility = View.GONE
                    goNextClearTop(MainActivity::class.java)
                }
                is SignInUiState.Error -> {
                    binding.mprogressBar.visibility = View.GONE
                    snack(binding.root, state.msg)
                }
            }
        }
    }
}
```

---

## Dependency Injection Configuration

### Improved DI Modules

**File:** `di/RepositoryModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFamilyRepository(
        impl: FamilyRepositoryImpl
    ): FamilyRepository
}
```

**File:** `di/UseCaseModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideLoginUseCase(
        authRepository: AuthRepository
    ): LoginUseCase = LoginUseCase(authRepository)

    @Provides
    @Singleton
    fun provideSignUpUseCase(
        authRepository: AuthRepository
    ): SignUpUseCase = SignUpUseCase(authRepository)
}
```

---

## Benefits of This Architecture

| Aspect | Benefit |
|--------|---------|
| **Testability** | Each layer can be tested in isolation |
| **Reusability** | Use cases can be reused in different UIs |
| **Maintainability** | Clear separation of concerns |
| **Scalability** | Easy to add features without affecting existing code |
| **Independence** | Business logic is independent of Android |
| **Flexibility** | Easy to swap data sources (local ↔ remote) |

---

## Migration Checklist

- [ ] Create `domain/` layer with entities and use cases
- [ ] Create use case interfaces (sealed results)
- [ ] Implement repository interfaces in `data/`
- [ ] Create proper DTOs for API responses
- [ ] Add Room database setup with DAOs and entities
- [ ] Create DI modules (RepositoryModule, UseCaseModule)
- [ ] Refactor ViewModels to use use cases via Flow
- [ ] Update Activities/Fragments to observe state only
- [ ] Add unit tests for domain and data layers
- [ ] Add integration tests for repository layer
- [ ] Remove hardcoded strings (already done in strings.xml)
- [ ] Add Timber logging for debugging

---

## Best Practices

1. **One Use Case = One Job**: Don't bloat use cases with multiple responsibilities
2. **Flow from ViewModel**: Use StateFlow for UI state, not LiveData (coroutine-native)
3. **Immutable State**: Always create new state objects, never mutate
4. **Error Handling**: Use Result wrapper, never throw exceptions in flows
5. **No UI Logic in Domain**: Domain must be Android-independent
6. **Repository as Single Source of Truth**: Repositories decide local vs. remote
7. **Proper Scoping**: Use viewModelScope for lifecycle-aware coroutine cancellation
8. **Hilt for Injection**: Never instantiate dependencies manually

---

## References
- [MVVM Architecture](https://developer.android.com/jetpack/guide/architecture)
- [Clean Architecture](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Kotlin Coroutines & Flow](https://developer.android.com/kotlin/flow)
