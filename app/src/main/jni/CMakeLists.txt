cmake_minimum_required(VERSION 3.4.1)

add_library(lowlevel-files SHARED
            lowlevelutils.c)

# Include libraries needed for lowlevel-files lib
target_link_libraries(lowlevel-files
                      android
                      log)
