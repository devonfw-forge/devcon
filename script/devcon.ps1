##################################################################################
# Devcon runner & installer 0.1 - Copyright (c) 2016 Capgemini 
##################################################################################

$devconhome = "${env:USERPROFILE}/.devcon"
$devconfile = "${devconhome}/devcon.jar"

function download() {
  #$url = "https://github.com/oasp/oasp4j/archive/release/2.0.0.zip"
  $url = "file:///D:/src/devcon/target/devcon.jar"
  $output = $devconfile
  $start_time = Get-Date

  Invoke-WebRequest -Uri $url -OutFile $output
  Write-Output "Time taken: $((Get-Date).Subtract($start_time).Seconds) second(s)"
}

if (Test-Path $devconfile){
  java -jar $devconfile @args
} else {
  echo "Devcon not present in ${devconhome}. Intenting to download and install it."
  mkdir $devconhome | Out-Null
  download
  global:ADD-PATH $devconhome | Out-Null
  copy $MyInvocation.MyCommand.Path $devconhome | Out-Null
  copy "${devconhome}/devcon.ps1" "${devconhome}/devon.ps1" | Out-Null
  echo "Devcon installed in ${devconhome} and added to the %PATH% environment variable"
  echo "Start a new console and run 'devon help guide' to see available commands"
}


##################################################################################
#  function to add to system PATH 
#  from https://gallery.technet.microsoft.com/scriptcenter/3aa9d51a-44af-4d2a-aa44-6ea541a9f721?SRC=Home
##################################################################################

Function global:TEST-LocalAdmin() 
    { 
    Return ([security.principal.windowsprincipal] [security.principal.windowsidentity]::GetCurrent()).isinrole([Security.Principal.WindowsBuiltInRole] "Administrator") 
    } 
     
Function global:SET-PATH() 
{ 
[Cmdletbinding(SupportsShouldProcess=$TRUE)] 
param 
( 
[parameter(Mandatory=$True,  
ValueFromPipeline=$True, 
Position=0)] 
[String[]]$NewPath 
) 
 
If ( ! (TEST-LocalAdmin) ) { Write-Host 'Need to RUN AS ADMINISTRATOR first'; Return 1 } 
     
# Update the Environment Path 
 
if ( $PSCmdlet.ShouldProcess($newPath) ) 
{ 
Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH –Value $newPath 
 
# Show what we just did 
 
Return $NewPath 
} 
} 
 
Function global:ADD-PATH() 
{ 
[Cmdletbinding(SupportsShouldProcess=$TRUE)] 
param 
    ( 
    [parameter(Mandatory=$True,  
    ValueFromPipeline=$True, 
    Position=0)] 
    [String[]]$AddedFolder 
    ) 
 
If ( ! (TEST-LocalAdmin) ) { Write-Host 'Need to RUN AS ADMINISTRATOR first'; Return 1 } 
     
# Get the Current Search Path from the Environment keys in the Registry 
 
$OldPath=(Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).Path 
 
# See if a new Folder has been supplied 
 
IF (!$AddedFolder) 
    { Return ‘No Folder Supplied.  $ENV:PATH Unchanged’} 
 
# See if the new Folder exists on the File system 
 
IF (!(TEST-PATH $AddedFolder)) 
    { Return ‘Folder Does not Exist, Cannot be added to $ENV:PATH’ } 
 
# See if the new Folder is already IN the Path 
 
$PathasArray=($Env:PATH).split(';') 
IF ($PathasArray -contains $AddedFolder -or $PathAsArray -contains $AddedFolder+'\') 
    { Return ‘Folder already within $ENV:PATH' } 
 
If (!($AddedFolder[-1] -match '\')) { $Newpath=$Newpath+'\'} 
 
# Set the New Path 
 
$NewPath=$OldPath+';’+$AddedFolder 
if ( $PSCmdlet.ShouldProcess($AddedFolder) ) 
{ 
Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH –Value $newPath 
 
# Show our results back to the world 
 
Return $NewPath  
} 
} 
 
FUNCTION GLOBAL:GET-PATH() 
{ 
Return (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).Path 
} 
 
Function global:REMOVE-PATH() 
{ 
[Cmdletbinding(SupportsShouldProcess=$TRUE)] 
param 
( 
[parameter(Mandatory=$True,  
ValueFromPipeline=$True, 
Position=0)] 
[String[]]$RemovedFolder 
) 
 
If ( ! (TEST-LocalAdmin) ) { Write-Host 'Need to RUN AS ADMINISTRATOR first'; Return 1 } 
     
# Get the Current Search Path from the Environment keys in the Registry 
 
$NewPath=(Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).Path 
 
# Verify item exists as an EXACT match before removing 
$Verify=$newpath.split(';') -contains $RemovedFolder 
 
# Find the value to remove, replace it with $NULL.  If it’s not found, nothing will change 
 
If ($Verify) { $NewPath=$NewPath.replace($RemovedFolder,$NULL) } 
 
# Clean up garbage from Path 
 
$Newpath=$NewPath.replace(';;',';') 
 
# Update the Environment Path 
if ( $PSCmdlet.ShouldProcess($RemovedFolder) ) 
{ 
Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH –Value $newPath 
 
# Show what we just did 
 
Return $NewPath 
} 
} 