cmake_minimum_required(VERSION 3.22.1)

project("govformai_native")

add_library(
    govformai_native
    SHARED
    native_lib.cpp
)

find_library(
    log-lib
    log
)

target_link_libraries(
    govformai_native
    ${log-lib}
)
