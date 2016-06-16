##################################################################################
 $COPYRIGHT = "Devcon runner & installer 0.2 - Copyright (c) 2016 Capgemini"
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

try {
    if (Test-Path $devconfile){
      java -jar $devconfile @args
    } else {
      echo $COPYRIGHT
      echo "Devcon not present in ${devconhome}. Intenting to download and install it."
      mkdir $devconhome | Out-Null
      download
      setx PATH "$env:path;${devconhome}" -m  | Out-Null
      $content = "@echo off`njava -jar ${devconfile} %*"

      [IO.File]::WriteAllLines("${devconhome}/devon.cmd", $content)
      [IO.File]::WriteAllLines("${devconhome}/devcon.cmd", $content)

      echo "Devcon installed in ${devconhome} and added to the %PATH% environment variable"
      echo "Start a new console and run 'devon help guide' to see available commands"
}
} catch {
    echo "Unexpected error. Reason: " + $_.Exception.Message
}
