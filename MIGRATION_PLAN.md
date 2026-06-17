# InstaResolv — KMP Non-Shared UI → Compose Multiplatform Shared UI Migration Plan

## Executive Summary

**Feasibility: HIGH.** The `:shared` module already has the full Compose Multiplatform stack on
`commonMain` (runtime, foundation, material3, ui, components.resources) and already declares iOS
framework targets. The business layer (ViewModels, repositories, models, networking, validation,
DI) is 100% shared already. This migration relocates the existing Android Compose UI into
`commonMain` and replaces the SwiftUI iOS UI with the shared Compose UI.

## Current Architecture

- `shared/commonMain`: data/domain/di/network/<feature>VM — all shared, no changes needed.
- `androidApp`: real UI (Compose), navigation (`androidx.navigation`), resources (drawables/fonts/strings).
- `iosApp`: real UI (SwiftUI), bridges to shared ViewModels via `ViewModelFactory` + Koin.

## Target Architecture

Promote all UI into `shared/commonMain/ui/{screens,components,theme,navigation}` and
`shared/commonMain/composeResources/{drawable,font,values}`. Android/iOS app modules become thin
shells that just host the shared `App()` composable.

Navigation: adopt `org.jetbrains.androidx.navigation:navigation-compose` (CMP-compatible,
near-drop-in replacement for the current `androidx.navigation` API used in `AppNavigation.kt`).

## Migration Roadmap

0. **Foundation & Resources** — move drawables/fonts/strings to `composeResources`; move
   `AppColors`/`AppTypography`/`textStyle` to `commonMain`, swap `R.*` → `Res.*`.
1. **Shared Components** — move `AppPrimaryButton`, `AppBorderButton`, `AppTextField`, `AppToast`/
   `ToastHost`, `NavigationBackIcon`, `AppTabBar` to `commonMain`.
2. **Navigation Migration** — add CMP nav dependency, port `AppNavigation.kt`/`Screens.kt` to
   `commonMain`, rewrite `App.kt` to host real `NavHost`.
3. **Screen Migration** — move screens in order: Splash → Welcome → ForgetPassword → Login →
   Register → OTP → Home/ProjectList → TabBar.
4. **iOS Shell Cutover** — point `ContentView` at `ComposeView()`, reconcile localization strings,
   delete SwiftUI files after parity sign-off.
5. **Cleanup** — remove dead Android resources, `ViewModelFactory`, unused Android nav dependency.

## Risks

- R-1: Android/iOS UIs already diverge in places (e.g. Login "register now" link). Default to
  Android behavior as canonical unless told otherwise.
- R-2: Home/ProjectList screens exist only on Android; iOS gains them as new functionality.
- R-3: iOS visual regression risk (fonts, safe-area, IME, toast animation) vs. native SwiftUI.
- R-4: Navigation library swap semantics (back stack, popUpTo/inclusive).
- R-5: Resource migration completeness (every `R.*` needs a `Res.*` counterpart).
- R-6: CMP/Material3 alpha/beta versions — possible API churn.

## Execution Note

Per user instruction, this migration is being executed directly (not staged behind further
review). Each phase is applied as a working commit-able state; Android build is kept green
throughout, iOS SwiftUI is removed only in the final phase after the shared UI is wired in.
