cmake_minimum_required(VERSION 3.22.1)

project("virtue-native")

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Include directories
include_directories(src/main/cpp)
include_directories(src/main/jni)

# Source files
file(GLOB_RECURSE NATIVE_SOURCES
    "src/main/cpp/*.cpp"
    "src/main/jni/*.cpp"
)

# Create shared library
add_library(virtue-native SHARED ${NATIVE_SOURCES})

# Link libraries
find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(virtue-native
    ${log-lib}
    ${android-lib}
)
