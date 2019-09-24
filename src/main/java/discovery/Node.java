/*
 *  Copyright (c) 2018 Isa Hekmatizadeh.
 *
 *  This file is part of Geev.
 *
 *  Geev is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Geev is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Geev.  If not, see <http://www.gnu.org/licenses/>.
 */

package discovery;

import java.util.Objects;

/**
 * Immutable class represents nodes. Each node has a role name, an IP address and a port number.
 * Equality of the nodes determined just by the IP address and port number.
 *
 * @author Isa Hekmatizadeh
 */
public class Node {
  private final String ip;
  private final int port;

  /**
   * Constructor of Node class
   *
   * @param ip   IP address of the node
   * @param port port number of the node
   */
  public Node(String ip, int port) {
    this.ip = ip;
    this.port = port;
  }

  public String getIp() {
    return ip;
  }

  public int getPort() {
    return port;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Node node = (Node) o;
    return port == node.port &&
        Objects.equals(ip, node.ip);
  }

  @Override
  public int hashCode() {
    return Objects.hash(ip, port);
  }

  @Override
  public String toString() {
    return "[" + ip + ":" + port + "]";
  }
}
