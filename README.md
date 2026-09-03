# WWF Project Management System – Android

Android app for tracking WWF conservation projects, tasks, and milestones.

## Overview
This project helps WWF teams manage conservation initiatives by tracking:

- projects
- tasks
- milestones
- progress updates

## Purpose
The app is designed to support project coordination and monitoring for environmental conservation work on Android devices.

## Home screen
The app's home screen is a native Jetpack Compose port of the public
[WWF Project Platform](https://gentle-dune-0405ec500.1.azurestaticapps.net/) landing page
(`ui/home/HomeScreen.kt`), laid out as a 3-page horizontal swipe with dot indicators:

1. Title section – hero badge, WWF logo (also the app's launcher icon), gradient title, live
   clock, Login button (sign-in dialog), Privacy/Terms links
2. Straw Headed Bulbul section
3. Rifle Range Road section

Each page is centred in the window and scrolls vertically when it does not fit. Project
sections sit directly on the page background (no card container, no "Project N of M"
counter, no chips) and use the site's `.info-content` styling (green 24sp bold title, 18sp
grey body, cyan "View …" text button) with uncropped, proportionally sized images. Every
project page uses the same fixed slots – image area, one title line, five description
lines, button – so the picture, title, description and button sit at exactly the same
position on every project regardless of description length. The app follows the system
light/dark setting (dark mode uses a darker gradient and lighter text), is portrait-only and
runs full screen – the Android navigation bar is hidden (swipe up from the bottom edge to
reveal it temporarily).

Source layout – every page and every section lives in its own file:

```
ui/home/
  HomeScreen.kt          pager + login dialog (entry point)
  LoginDialog.kt         native sign-in modal
  SwipePage.kt           per-page container: centring, scrolling, swipe transition
  PagerControls.kt       bottom strip: animated, tappable page dots
  InfoColors.kt          .info-content colours (light + dark)
  pages/
    HeroPage.kt          page 1 – title / hero
    ProjectPage.kt       pages 2..n – one project each
  sections/
    LegalLinks.kt        Privacy Policy | Terms of Service
    ProjectImage.kt      proportional, tappable artwork
    ProjectText.kt       ProjectTitle + ProjectDescription (fixed line slots)
    ViewProjectButton.kt "View <project>" button
    SignedInBanner.kt    "Signed in as …" + Logout (replaces the Login button)
ui/project/
  ProjectScreen.kt       project page (/StrawheadedBulbul, /RifleRangeRoad) – 4-page swipe
  pages/
    ProjectHeroPage.kt    page 1 – title, clock, CTAs
    ProjectToolsPage.kt   page 2 – feature cards
    ProjectGalleryPage.kt page 3 – photo & video gallery (backend)
    ProjectInfoPage.kt    page 4 – background, stats, painting
  sections/
    ProjectHeroSection.kt badge, logo, "WWF <project> Survey Platform", clock, tool buttons
    FeaturesSection.kt   "Comprehensive Conservation Tools" – 3 cards per row (2 if too narrow), scaled to fit
    GallerySection.kt    All/Photos/Videos filter, media grid (all items), loading/error states
    InfoSection.kt       background copy, statistics tiles, painting with caption
  gallery/
    GalleryViewModel.kt  loads the backend listing; owns the GalleryRepository
    MediaTile.kt         grid tile: progress ring, retry, thumbnail, play badge
    FullScreenGallery.kt lightbox: swipe between items, pinch-zoom photos, inline video
    VideoPlayer.kt       Media3 ExoPlayer wrapper (plays only the active page)
    MediaDecoder.kt      size-aware bitmap / video-frame decoding with EXIF rotation + memory cache
ui/navigation/
  AppNavHost.kt          Home <-> Project navigation (system back returns home)
ui/components/           shared building blocks, one per file
  Layout.kt              WindowSizeProvider, MaxContentWidth, scaledSp
  PageBackground.kt      gradient + radial glow (light / dark)
  HeroBadge.kt, HeroLogo.kt, HeroTitle.kt, HeroSubtitle.kt, LiveDateTime.kt
  GradientCtaButton.kt, ActionButton.kt
  PressScale.kt, Reveal.kt, GlassPill.kt, StatChip.kt, ThemeUtils.kt
data/
  Project.kt             project catalogue (images, copy, info section)
  local/LocalSession.kt  device-local, simulated session (any email + password)
  remote/
    GalleryApi.kt        SHB: POST /gallery list + stream; RRR: POST /rifleRangeRoad/surveys + GET image URLs
    GalleryRepository.kt in-memory media (never written to disk), prefetched at launch, per-file fetch state
    GalleryMedia.kt      id / title / mimeType / optional direct url (isVideo)
    StatsApi.kt          SHB: POST /surveys getPublicStatistics; RRR: derived from /rifleRangeRoad/surveys records
    StatsRepository.kt   per-project ProjectStats state (Loading / Ready / Failed), prefetched at launch
    ProjectStats.kt      observations / locations / volunteers / yearsActive (display strings)
```

Dashboard-style interactivity:

- The project image and the "View …" button both open the project page.
- Staggered fade/slide-in reveal of content, pages fade and scale while swiping, animated
  page dots, and press-scale feedback on every tappable element.
- Bottom strip with animated, tappable page dots.

### Responsive layout
Layout adapts to the window size rather than the device type (`ui/WindowSize.kt`):

| Width class | Range     | Typical device                          | Behaviour                                   |
|-------------|-----------|-----------------------------------------|---------------------------------------------|
| Compact     | < 600dp   | Phones (portrait)                       | Single column, full-width CTA, image on top |
| Medium      | 600–839dp | Phones (landscape), small tablets, folds | Larger type/logo, side-by-side project cards |
| Expanded    | ≥ 840dp   | Tablets, landscape tablets, desktop     | Max-width content, largest type/logo        |

Short windows (< 480dp tall, e.g. phone landscape) shrink the hero so it stays usable.
All text uses `sp`, touch targets are ≥ 44dp and system bars/cutouts are handled via
`WindowInsets.safeDrawing`.
## Scope
The home page and the two project pages are implemented, fully native (no WebView). The only
network use is the gallery, which needs the `INTERNET` permission.

**Public access with optional login.** The app opens straight to the home page and every
project page can be browsed without signing in – nothing redirects to a login. Login lives on
the home page: the hero's Login button opens the sign-in dialog (device-local, simulated: any
email + password), after which the hero shows "Signed in as …" with Logout. On a project page
the signed-out state shows "Go to Login" buttons (hero and each tool card) that return to the
home page with the dialog open; once signed in the tools (Explore Dashboard, Survey Event
Management, Settings, Logout) appear instead. Rifle Range Road's survey tools are "Coming soon"
as on the web. Each project page is itself a 4-page swipe (hero / tools / gallery / info) mirroring the
home page; there is no on-screen back button – the system back gesture returns home. The
dashboard / survey / settings tools and the legal links show a "not available yet" message until
they are built.

**Statistics from the backend.** The info page's stat tiles mirror the web: Straw-headed Bulbul
uses `fetchSurveyDataForHomePage()` → `POST /surveys {purpose: "getPublicStatistics",
databaseName: "StrawHeadedBulbul"}` (`totalObservations`, `uniqueLocations`, `userCount`,
`numberOfYears`); Rifle Range Road uses `getRifleRangeRoadSurveyData()` → `POST
/rifleRangeRoad/surveys {purpose: "retrieve"}` and derives the numbers from the records (one
observation per record, distinct "Name of Surveyors" as volunteers, years since the earliest
"Survey Date"). Tiles show "…" while loading and "—" if the request fails; both projects are
prefetched at launch.

**Gallery from the backend.** Each project has its own gallery source on
`https://shb-backend.azurewebsites.net` (public, no auth). Straw-headed Bulbul mirrors the web's
`getGalleryImages()` / `streamImage()`: `POST /gallery {purpose: "gallery"}` lists every photo
and video and `{purpose: "stream", fileId}` returns the raw file. Rifle Range Road mirrors
`getRifleRangeRoadSurveyData()`: `POST /rifleRangeRoad/surveys {purpose: "retrieve"}` returns the
survey records and the gallery is the distinct `Image URL` of each record (fetched with a GET).
Files are only served whole, so each file is streamed straight into memory while its tile / viewer page shows the progress
percentage (10-minute timeout like the web). `WwfApplication` starts prefetching every photo and
video of both projects at launch so the galleries are ready to view (a priority queue with 6
workers lets whatever is on screen jump ahead of the background prefetch; Typeform sends no
Content-Length, so Rifle Range Road tiles show an indeterminate spinner); photos are re-encoded to 2048 px on
arrival so the set fits in memory (`largeHeap`, bounded cache of half the heap, re-fetched if
evicted). Nothing is saved to storage, tiles and the viewer show no file names, and the window
is `FLAG_SECURE` (viewer dialog too) so media can't be screenshotted or screen-recorded. The
viewer swipes between items, pinch/double-tap zooms photos and plays videos inline with
ExoPlayer controls from `ByteArrayDataSource` (only the visible page plays; playback pauses when
the app is backgrounded). Filters
All / Photos / Videos match on MIME type, every item is shown at once, and
loading, error (Retry) and empty states are handled.

The launcher icon is the WWF logo (`drawable-nodpi/ic_launcher_logo.png`), the same image
shown as the hero of the home page and the project pages.
