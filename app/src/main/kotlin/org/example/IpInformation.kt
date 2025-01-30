class IpInformation(val ip: String, val port: UInt) {
    override fun toString(): String {
        return "(IP=${this.ip}, Port=${this.port})"
    }
}