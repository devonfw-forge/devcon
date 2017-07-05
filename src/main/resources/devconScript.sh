echo "Executing script"
export PATH=$PATH:$1

if grep -Fxq '#START_HERE' /home/ssarmoka/.profile
then
  sed -i '/^#START_HERE/,/^#END_HERE/{/^#/!{/^\$/!d}}' /home/ssarmoka/.profile
  sed -i '/#START_HERE/ a export PATH='$PATH:"\$PATH" /home/ssarmoka/.profile
else
  echo "#START_HERE" >> ~/.profile
  echo export PATH=$PATH:"\$PATH" >> ~/.profile
  echo "#END_HERE">> ~/.profile
fi
echo "Path in .profile file is modified for Devcon"
