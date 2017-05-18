if [ $# -lt 2 ]
then
  echo "Usage: $0 [target_var_dir] [target_host]"
  exit
fi

ansible-playbook playbook.yml --extra-vars "base_dir=$1 variable_host=$2"
