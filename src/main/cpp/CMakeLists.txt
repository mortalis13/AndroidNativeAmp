cmake_minimum_required(VERSION 3.4.1)
project(amp LANGUAGES C CXX)

find_package(oboe REQUIRED CONFIG)

add_library(amp SHARED
    LiveEffectEngine.cpp
    FullDuplexStream.cpp
    jni_bridge.cpp
)

target_link_libraries(amp PRIVATE oboe::oboe android atomic log)
