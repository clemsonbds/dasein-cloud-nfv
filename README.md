# dasein-cloud-nfv

##Project Objective:
The objective of this project is to develop a proof of concept that [Dasein](http://www.dasein.org/) can be used to standardize the Nf-Vi Interface (interface the VIM uses to manage the NFVI) according to the [ETSI specifications](http://www.etsi.org/deliver/etsi_gs/NFV-MAN/001_099/001/01.01.01_60/gs_nfv-man001v010101p.pdf).We use Dasein to abstract the infrastructure APIs and features into one consumable by NFV managers.

##Project structure

The project is structured into 4 parts which are Compute, Storage, Networking and hypervisor.

Below are the compute operations implemented and a short description of what they do

| Compute Operations  | Description |
|---------------------|------------------------------------------|
| Create | Create and start a VM |
| Shutdown | Shutdown a VM |
| Update | Update a created VM |
| Destroy | Stop a VM |
| Reboot | Reboot a VM |
| Suspend | Pause a VM |
| Resume | Unpause a VM |
| Save | Stops the VM and save the data to a file |
| Restore | Resume a VM |
| List | List the VM for specific cloud provider |
| Query | Not implemented |
