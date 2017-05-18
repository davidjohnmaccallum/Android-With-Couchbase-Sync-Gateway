# SimpleSyncGateway

A simple Couchbase Sync Gateway example.

To get started install Docker and Ansible and run:

`
./run_playbook.sh "$PWD/var" localhost
`

Once Sync Gateway has started you will be able to access the Public REST interface http://localhost:4984 and the Admin interface http://localhost:4985/_admin/.

You can view the Sync Gateway logs as follows:

`
david$ docker logs -f sample-sync-gateway
`

Here is a successful statup log:

2017-05-18T09:18:45.795Z Enabling logging: [*]
2017-05-18T09:18:45.795Z ==== Couchbase Sync Gateway/1.4.0(2;9e18d3e) ====
2017-05-18T09:18:45.795Z requestedSoftFDLimit < currentSoftFdLimit (5000 < 1048576) no action needed
2017-05-18T09:18:45.795Z Opening db /db as bucket "db", pool "default", server <walrus:>
2017-05-18T09:18:45.795Z Opening Walrus database db on <walrus:>
2017-05-18T09:18:45.797Z Cache: Initializing changes cache with options {ChannelCacheOptions:{ChannelCacheMinLength:0 ChannelCacheMaxLength:0 ChannelCacheAge:0s} CachePendingSeqMaxWait:5s CachePendingSeqMaxNum:10000 CacheSkippedSeqMaxWait:1h0m0s}
2017-05-18T09:18:45.799Z Auth: Saved _sync:user:: {"admin_channels":{"*":1},"all_channels":null,"sequence":1,"rolesSince":null}
2017-05-18T09:18:45.799Z     Reset guest user to config
2017-05-18T09:18:45.799Z Changes+: Notifying that "db" changed (keys="{_sync:user:}") count=2
2017-05-18T09:18:45.799Z Starting admin server on :4985
2017-05-18T09:18:45.800Z Cache: Received #1 ("_user/")
2017-05-18T09:18:45.800Z Cache: Initialized cache for channel "*" with options: &{ChannelCacheMinLength:50 ChannelCacheMaxLength:500 ChannelCacheAge:1m0s}
2017-05-18T09:18:45.800Z Cache:     #1 ==> channel "*"
2017-05-18T09:18:45.800Z Changes+: Notifying that "db" changed (keys="{*}") count=3
2017-05-18T09:18:45.808Z Starting server on :4984 ...


#### Useful Documentation

* Sync Gateway Docs https://developer.couchbase.com/documentation/mobile/1.4/guides/sync-gateway/index.html
* Ansible Docker Module Docs https://docs.ansible.com/ansible/docker_container_module.html