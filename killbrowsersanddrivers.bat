@echo off
rem   just kills stray local chromedriver.exe,geckodriver.exe instances.
rem   useful if you are trying to clean your project, and your ide is complaining.

taskkill /im chromedriver.exe /f

taskkill /im chrome.exe /f

taskkill /im geckodriver.exe /f

taskkill /im msedgedriver.exe /f

taskkill /im IEDriverServer.exe /f

taskkill /im phantomjs.exe /f