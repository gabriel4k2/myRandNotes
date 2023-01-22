#

#
#
#
#
#
#
#
#
#

# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.22.1)
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -fopenmp=libomp ")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fopenmp=libomp ")
set(LOCAL_LDFLAGS "${LOCAL_LDFLAGS}  -lomp  ")
set(LOCAL_CFLAGS "${LOCAL_CFLAGS} -fopenmp ")
set(LOCAL_CXXFLAGS "${LOCAL_CXXFLAGS} -fopenmp ")
# Declares and names the project.

project("fluidsynthdemo")
set(distribution_DIR ${CMAKE_SOURCE_DIR}/..)
add_library(lib_fluidsynth_asset SHARED IMPORTED)
add_library(lib_fluidsynth SHARED IMPORTED)

add_library(libFLAC SHARED IMPORTED)
add_library(libgio-2.0 SHARED IMPORTED)

add_library(libglib-2.0 SHARED IMPORTED)
add_library(libgmodule-2.0 SHARED IMPORTED)

add_library(libgobject-2.0 SHARED IMPORTED)
add_library(libgthread-2.0 SHARED IMPORTED)

add_library(libinstpatch-1.0 SHARED IMPORTED)
add_library(liboboe SHARED IMPORTED)

add_library(libogg SHARED IMPORTED)
add_library(libopus SHARED IMPORTED)

add_library(libsndfile SHARED IMPORTED)
add_library(libvorbis SHARED IMPORTED)

add_library(libvorbisenc SHARED IMPORTED)
add_library(libvorbisfile SHARED IMPORTED)

add_library(libpcre SHARED IMPORTED)
add_library(libpcreposix SHARED IMPORTED)



add_library(omp SHARED IMPORTED)
set_target_properties(omp PROPERTIES IMPORTED_LOCATION
        /home/gabriel/Android/Sdk/ndk/25.1.8937393/toolchains/llvm/prebuilt/linux-x86_64/lib64/clang/14.0.6/lib/linux/x86_64/libomp.so

        )


set_target_properties(libFLAC PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libFLAC.so
)
set_target_properties(libgio-2.0 PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libgio-2.0.so
)
set_target_properties(libglib-2.0 PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libglib-2.0.so
        )
set_target_properties(libgmodule-2.0 PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libgmodule-2.0.so
)


set_target_properties(libgobject-2.0 PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libgobject-2.0.so
)


set_target_properties(libgthread-2.0 PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libgthread-2.0.so
        )

set_target_properties(libinstpatch-1.0 PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libinstpatch-1.0.so
)

set_target_properties(liboboe PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/liboboe.so
)

set_target_properties(libogg PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libogg.so
)

set_target_properties(libopus PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libopus.so
)

set_target_properties(libsndfile PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libsndfile.so
)

set_target_properties(libvorbis PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libvorbis.so
)

set_target_properties(libvorbisenc PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libvorbisenc.so
)

set_target_properties(libvorbisfile PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libvorbisfile.so
)

set_target_properties(libpcre PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libpcre.so
)
set_target_properties(libpcreposix PROPERTIES IMPORTED_LOCATION
${distribution_DIR}/jniLibs/x86_64/lib/libpcreposix.so
)



add_library(fluidsynthdemo SHARED fluidsynthdemo.cpp )
target_include_directories(fluidsynthdemo PRIVATE
${distribution_DIR}/jniLibs/x86_64/include
)

set_target_properties(lib_fluidsynth_asset PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/jniLibs/x86_64/lib/libfluidsynth-assetloader.so
        )


set_target_properties(lib_fluidsynth PROPERTIES IMPORTED_LOCATION
        ${distribution_DIR}/jniLibs/x86_64/lib/libfluidsynth.so
        )



set_target_properties(lib_fluidsynth

        PROPERTIES COMPILE_OPTIONS -fopenmp
        )

set_target_properties(lib_fluidsynth PROPERTIES LINK_FLAGS "-static-openmp -fopenmp")
target_link_libraries(fluidsynthdemo lib_fluidsynth_asset
        libogg
libFLAC
libgio-2.0
libglib-2.0
libgmodule-2.0
libgobject-2.0
libgthread-2.0
libinstpatch-1.0
        lib_fluidsynth
        liboboe

libopus
libsndfile
libvorbis
libvorbisenc
libvorbisfile
libpcre
libpcreposix
omp
)