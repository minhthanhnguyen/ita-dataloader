#!/usr/bin/env bash

#You'll need to log into Azure first 'sudo az acr login'
cd docker
sudo az acr login --name itacontainerregistry
sudo docker login itacontainerregistry.azurecr.io -u ITAContainerRegistry -p $AZURE_CONTAINER_KEY  #use container username and access key
sudo docker build -t itacontainerregistry.azurecr.io/susa-stats-dataloader .
sudo docker push itacontainerregistry.azurecr.io/susa-stats-dataloader

#FYI: Registry credentials are autogenerated and can be procured from portal.azure.com
# sudo docker login

cd ..

kubectl apply -f kube-config.yml
